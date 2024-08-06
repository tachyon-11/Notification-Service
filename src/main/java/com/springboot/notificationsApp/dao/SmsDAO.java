package com.springboot.notificationsApp.dao;

import com.springboot.notificationsApp.entity.SMS;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.List;

@Repository
public interface SmsDAO extends CrudRepository<SMS, Integer> {
    List<SMS> findAll();
    Optional<SMS> findById(int id);
    SMS save(SMS sms);
    void deleteById(int id);
}
