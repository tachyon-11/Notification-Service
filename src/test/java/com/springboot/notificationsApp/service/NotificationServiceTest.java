package com.springboot.notificationsApp.service;

import com.springboot.notificationsApp.dao.NumberDAO;
import com.springboot.notificationsApp.dao.SmsDAO;
import com.springboot.notificationsApp.dto.SmsDTO;
import com.springboot.notificationsApp.elasticsearch.queryService.ElasticSearchQuery;
import com.springboot.notificationsApp.entity.PhoneNumber;
import com.springboot.notificationsApp.entity.SMS;
import com.springboot.notificationsApp.response.SmsResponse;
import com.springboot.notificationsApp.exceptionHandling.NotificationException;
import com.springboot.notificationsApp.kafka.producer.KafkaProducerService;
import com.springboot.notificationsApp.redis.CacheRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock
    private SmsDAO messageDAO;
    @Mock
    private NumberDAO phoneDAO;
    @Mock
    private KafkaProducerService kafkaProducerService;
    @Mock
    private CacheRepository redisRepository;
    @Mock
    private ElasticSearchQuery elasticSearchQuery;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private SMS sms;
    private PhoneNumber phoneNumber;
    private SmsDTO newSmsDTO;

    @Test
    @DisplayName("Testing saving of entry")
    void saveSMS() {
        SmsDTO newSmsDTO = new SmsDTO("9354994119", "Testing...");
        SMS newSMS = new SMS();
        newSMS.setId(1);
        newSMS.setMessage(newSmsDTO.getMessage());
        newSMS.setPhoneNumber(newSmsDTO.getPhoneNumber());
        when(messageDAO.save(any(SMS.class))).thenReturn(newSMS);

        SmsResponse response = notificationService.save(newSmsDTO);

        assertEquals("Testing...", response.getMessage());
        assertEquals("1", response.getId());
    }

    @Test
    @DisplayName("Testing find by id")
    void findSMSById() {
        SmsDTO newSmsDTO = new SmsDTO("9354994119", "Testing...");
        SMS newSMS = new SMS();
        newSMS.setId(1);
        newSMS.setMessage(newSmsDTO.getMessage());
        newSMS.setPhoneNumber(newSmsDTO.getPhoneNumber());
        when(messageDAO.findById(1)).thenReturn(Optional.of(newSMS));

        SMS response = notificationService.findById(1);

        assertEquals("Testing...", response.getMessage());
        assertEquals(1, response.getId());
    }

    @Test
    @DisplayName("Testing if id not present")
    void findSMSByIdNotPresent() {
        when(messageDAO.findById(1)).thenReturn(Optional.empty());
        assertThrows(NotificationException.class, () -> notificationService.findById(1));
        verify(this.messageDAO).findById((anyInt()));
    }

    @Test
    @DisplayName("Delete the sms with a id")
    void deleteSMSById() {
        SmsDTO newSmsDTO = new SmsDTO("9354994119", "Testing...");
        SMS newSMS = new SMS();
        newSMS.setId(1);
        newSMS.setMessage(newSmsDTO.getMessage());
        newSMS.setPhoneNumber(newSmsDTO.getPhoneNumber());
        when(messageDAO.findById(1)).thenReturn(Optional.of(newSMS));
        notificationService.deleteSMS(1);
        verify(this.messageDAO).findById((anyInt()));
        verify(this.messageDAO).deleteById((anyInt()));
    }

    @Test
    @DisplayName("Throw exception on deleting with invalid id")
    void deleteSMSByIdNotPresent() {
        when(messageDAO.findById(1)).thenReturn(Optional.empty());
        assertThrows(NotificationException.class, () -> notificationService.deleteSMS(1));
        verify(this.messageDAO).findById((anyInt()));
    }

    @Test
    @DisplayName("List all available SMS")
    void listAllSMS() {
        SmsDTO newSmsDTO1 = new SmsDTO("9354994119", "Testing...");
        SMS newSMS1 = new SMS();
        newSMS1.setId(2);
        newSMS1.setMessage(newSmsDTO1.getMessage());
        newSMS1.setPhoneNumber(newSmsDTO1.getPhoneNumber());
        SmsDTO newSmsDTO2 = new SmsDTO("9911273817", "Testing again...");
        SMS newSMS2 = new SMS();
        newSMS2.setId(1);
        newSMS2.setMessage(newSmsDTO2.getMessage());
        newSMS2.setPhoneNumber(newSmsDTO2.getPhoneNumber());
        List<SMS> smsList = new ArrayList<>();
        smsList.add(newSMS1);
        smsList.add(newSMS2);

        when(messageDAO.findAll()).thenReturn(smsList);
        List<SMS> response = notificationService.queryForMessage();

        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("List all blocked numbers")
    void listAllNumbers() {
        PhoneNumber phoneNumber1 = new PhoneNumber("1234567890");
        PhoneNumber phoneNumber2 = new PhoneNumber("0987654321");
        List<PhoneNumber> phoneNumberList = new ArrayList<>();
        phoneNumberList.add(phoneNumber1);
        phoneNumberList.add(phoneNumber2);
        when(phoneDAO.findAll()).thenReturn(phoneNumberList);
        List<PhoneNumber> response = notificationService.queryForNumber();
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Delete numbers from blacklist when there's no exception")
    void saveNumbers() {
        List<String> phoneNumberList = new ArrayList<>();
        phoneNumberList.add("1234567890");
        phoneNumberList.add("0987654321");
        notificationService.addNumbers(phoneNumberList);
        verify(phoneDAO, times(1)).saveAll(any());
        verify(redisRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("Delete numbers from blacklist when there's no exception")
    void deleteNumbers() {
        List<String> phoneNumberList = new ArrayList<>();
        phoneNumberList.add("1234567890");
        phoneNumberList.add("0987654321");
        notificationService.deleteNumbers(phoneNumberList);
        verify(phoneDAO, times(1)).deleteAllById(any());
        verify(redisRepository, times(2)).delete(any());
    }

    @Test
    @DisplayName("Delete numbers from blacklist when there's exception trying to delete")
    void testDeleteNumbersThrowsException(){
        List<String> phoneNumberList = new ArrayList<>();
        phoneNumberList.add("1234567890");
        phoneNumberList.add("0987654321");

        Answer<Void> answer = new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) throws Throwable {
                throw new Exception("Error in Jedis");
            }
        };

        doAnswer(answer).when(redisRepository).delete(any());
        Assertions.assertThrows(Exception.class, ()->{
            notificationService.deleteNumbers(phoneNumberList);
        });
    }

    @Test
    @DisplayName("Find number in blacklist if it exist in cache")
    void findBlockedNumberFromCache(){
        when(redisRepository.exists("1234567890")).thenReturn(Boolean.TRUE);
        PhoneNumber response = notificationService.findByNumber("1234567890");
        assertEquals("1234567890", response.getPhoneNumber());
        verify(phoneDAO, times(0)).findById(any());
        verify(redisRepository, times(1)).exists(any());
    }

    @Test
    @DisplayName("Find number in blacklist if it exist only in DB")
    void findBlockedNumberFromDatabase(){
        when(redisRepository.exists("1234567890")).thenReturn(Boolean.FALSE);
        when(phoneDAO.findById("1234567890")).thenReturn(Optional.of(new PhoneNumber("1234567890")));
        PhoneNumber response = notificationService.findByNumber("1234567890");
        assertEquals("1234567890", response.getPhoneNumber());
        verify(phoneDAO, times(1)).findById(any());
        verify(redisRepository, times(1)).exists(any());
    }

    @Test
    @DisplayName("Find number in blacklist which dont exist")
    void findBlockedNumberWhichDontExist(){
        when(redisRepository.exists("1234567890")).thenReturn(Boolean.FALSE);
        when(phoneDAO.findById("1234567890")).thenReturn(Optional.empty());
        PhoneNumber response = notificationService.findByNumber("1234567890");
        assertEquals("Not found", response.getPhoneNumber());
        verify(phoneDAO, times(1)).findById(any());
        verify(redisRepository, times(1)).exists(any());
    }


}