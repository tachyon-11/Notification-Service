package com.springboot.notificationsApp.dao;

import com.springboot.notificationsApp.entity.PhoneNumber;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NumberDAO extends CrudRepository<PhoneNumber, String> {
    List<PhoneNumber> findAll();
    PhoneNumber save(PhoneNumber phoneNumber);
    Optional<PhoneNumber> findById(String number);
    void deleteAllById(Iterable<? extends String> phoneNumbers);
}
