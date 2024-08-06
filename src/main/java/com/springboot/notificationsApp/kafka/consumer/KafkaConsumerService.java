package com.springboot.notificationsApp.kafka.consumer;
import com.springboot.notificationsApp.elasticsearch.model.MessageModel;
import com.springboot.notificationsApp.elasticsearch.queryService.ElasticSearchQuery;
import com.springboot.notificationsApp.entity.PhoneNumber;
import com.springboot.notificationsApp.entity.SMS;
import com.springboot.notificationsApp.kafka.producer.KafkaProducerService;
import com.springboot.notificationsApp.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static com.springboot.notificationsApp.thirdpartyapi.RandomNumberGenerator.getRandomNumber;

@Slf4j
@Service
public class KafkaConsumerService {
    private final NotificationService smsService;
    private final ElasticSearchQuery elasticSearchQuery;
    private final KafkaProducerService kafkaProducerService;

    public KafkaConsumerService(NotificationService smsService, ElasticSearchQuery elasticSearchQuery, KafkaProducerService kafkaProducerService) {
        this.smsService = smsService;
        this.elasticSearchQuery = elasticSearchQuery;
        this.kafkaProducerService = kafkaProducerService;
    }

    public static Date convertToDate(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    @KafkaListener(topics = "new-message", groupId = "notificationService")
    public void processRequestId(String requestId) throws IOException {
        int theId = Integer.parseInt(requestId);
        SMS fetchedSMS =  smsService.findById(theId);
        PhoneNumber checkBlacklist = smsService.findByNumber(fetchedSMS.getPhoneNumber());
        if(checkBlacklist.getPhoneNumber().equals("Not found")){
            int prob = getRandomNumber();
            if(prob <= 3){
                smsService.updateMessage(fetchedSMS, "QUEUE", "TRYING TO SEND AGAIN");
                kafkaProducerService.sendRequestId("failed-message", requestId);
            }
            else{
                smsService.updateMessage(fetchedSMS, "SENT", "SUCCESSFULLY DELIVERED ON FIRST ATTEMPT");
                elasticSearchQuery.createOrUpdateMessage(new MessageModel(Integer.toString(fetchedSMS.getId()), fetchedSMS.getMessage(), convertToDate(fetchedSMS.getCreatedAt()),convertToDate(LocalDateTime.now())));
            }
        }
        else{
            smsService.updateMessage(fetchedSMS, "FAIL", "NUMBER IS BLACKLISTED");
        }
    }

    @KafkaListener(topics = "failed-message", groupId = "notificationService")
    public void processFailedMessage(String requestId) throws IOException {
        int theId = Integer.parseInt(requestId);
        SMS fetchedSMS =  smsService.findById(theId);
        PhoneNumber checkBlacklist = smsService.findByNumber(fetchedSMS.getPhoneNumber());
        if(checkBlacklist.getPhoneNumber().equals("Not found")){
            int prob = getRandomNumber();
            if(prob <= 3){
                smsService.updateMessage(fetchedSMS, "FAIL", "MESSAGE COULD NOT BE SENT");
                elasticSearchQuery.deleteMessageById(Integer.toString(fetchedSMS.getId()));
            }
            else{
                smsService.updateMessage(fetchedSMS, "SENT", "SUCCESSFULLY DELIVERED ON SECOND ATTEMPT");
                elasticSearchQuery.createOrUpdateMessage(new MessageModel(Integer.toString(fetchedSMS.getId()), fetchedSMS.getMessage(), convertToDate(fetchedSMS.getCreatedAt()),convertToDate(LocalDateTime.now())));
            }
        }
        else{
            smsService.updateMessage(fetchedSMS, "FAIL", "NUMBER IS BLACKLISTED");
        }
    }

}
