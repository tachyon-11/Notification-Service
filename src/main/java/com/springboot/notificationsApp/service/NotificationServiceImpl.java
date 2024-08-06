package com.springboot.notificationsApp.service;
import com.springboot.notificationsApp.dao.NumberDAO;
import com.springboot.notificationsApp.dao.SmsDAO;
import com.springboot.notificationsApp.dto.SmsDTO;
import com.springboot.notificationsApp.entity.PhoneNumber;
import com.springboot.notificationsApp.entity.SMS;
import com.springboot.notificationsApp.response.SmsResponse;
import com.springboot.notificationsApp.exceptionHandling.NotificationException;
import com.springboot.notificationsApp.kafka.producer.KafkaProducerService;
import com.springboot.notificationsApp.redis.CacheRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService{
    private final SmsDAO messageDAO;
    private final NumberDAO phoneDAO;
    private final KafkaProducerService kafkaProducerService;
    private final CacheRepository redisRepository;;

    @Autowired
    public NotificationServiceImpl(SmsDAO messageDAO, NumberDAO phoneDAO, KafkaProducerService kafkaProducerService, CacheRepository redisRepository) {
        this.messageDAO = messageDAO;
        this.phoneDAO = phoneDAO;
        this.kafkaProducerService = kafkaProducerService;
        this.redisRepository = redisRepository;
    }

    @Override
    @Transactional
    public void deleteSMS(int messageId) {
        Optional<SMS> deletedSms = messageDAO.findById(messageId);
        if(deletedSms.isPresent()) {
            messageDAO.deleteById(messageId);
        } else {
            throw new NotificationException("No message exist with " + messageId +" so cant delete");
        }
    }

    @Override
    public List<SMS> queryForMessage() {
        return this.messageDAO.findAll();
    }

    @Override
    public SMS findById(int theId) {
        Optional<SMS> smsOptional = messageDAO.findById(theId);
        if(smsOptional.isPresent()) {
            return smsOptional.get();
        } else {
            throw new NotificationException("Message id not found - " + theId);
        }
    }

    @Override
    @Transactional
    public SmsResponse save(SmsDTO newSmsDTO) {
        SMS newSMS = new SMS();
        newSMS.setMessage(newSmsDTO.getMessage());
        newSMS.setPhoneNumber("+91"+newSmsDTO.getPhoneNumber());
        newSMS.setCreatedAt(LocalDateTime.now());
        newSMS.setStatus("OPEN");
        SMS savedSMS = this.messageDAO.save(newSMS);
        String smsID = String.valueOf(savedSMS.getId());
        kafkaProducerService.sendRequestId("new-message", smsID);
        return new SmsResponse(newSmsDTO.getMessage(), smsID, LocalDateTime.now());
    }

    @Override
    public void updateMessage(SMS fetchedSMS, String status, String failureComments) {
        fetchedSMS.setStatus(status);
        fetchedSMS.setUpdatedAt(LocalDateTime.now());
        fetchedSMS.setFailureComments(failureComments);
        this.messageDAO.save(fetchedSMS);
    }

    @Override
    public List<PhoneNumber> queryForNumber() {
        return phoneDAO.findAll();
    }

    @Override
    public PhoneNumber findByNumber(String theNumber) {
        if(redisRepository.exists(theNumber))
            return new PhoneNumber(theNumber);
        Optional<PhoneNumber> numberOptional = phoneDAO.findById(theNumber);
        return numberOptional.orElseGet(() -> new PhoneNumber("Not found"));
    }

    @Override
    @Transactional
    public void addNumbers(List<String> phoneNumbers) {
        List<PhoneNumber> phoneNumberEntities = new ArrayList<>();
        for (String phoneNumber : phoneNumbers) {
            redisRepository.save(phoneNumber);
            phoneNumberEntities.add(new PhoneNumber(phoneNumber));
        }
        phoneDAO.saveAll(phoneNumberEntities);
    }

    @Override
    @Transactional
    public void deleteNumbers(List<String> phoneNumbers) {
        try {
            for (String phoneNumber : phoneNumbers) {
                redisRepository.delete(phoneNumber);
            }
            phoneDAO.deleteAllById(phoneNumbers);;
        }
        catch (Exception e){
            throw new NotificationException("Unable to delete from MySQL database and Cache");
        }

    }
}
