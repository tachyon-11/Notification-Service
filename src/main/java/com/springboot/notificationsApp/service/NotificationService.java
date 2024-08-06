package com.springboot.notificationsApp.service;

import com.springboot.notificationsApp.dto.SmsDTO;
import com.springboot.notificationsApp.entity.PhoneNumber;
import com.springboot.notificationsApp.entity.SMS;
import com.springboot.notificationsApp.response.SmsResponse;

import java.util.List;

public interface NotificationService {
    List<SMS> queryForMessage();

    SMS findById(int theId);

    SmsResponse save(SmsDTO newSmsDTO);

    void deleteSMS(int theId);

    List<PhoneNumber> queryForNumber();

    PhoneNumber findByNumber(String theNumber);

    void addNumbers(List<String> phoneNumbers);

    void deleteNumbers(List<String> phoneNumbers);

    void updateMessage(SMS theMessage, String status, String failureComments);

}
