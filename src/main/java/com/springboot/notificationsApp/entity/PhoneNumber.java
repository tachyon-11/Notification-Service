package com.springboot.notificationsApp.entity;

import jakarta.persistence.*;

@Entity
@Table(name="blacklist")
public class PhoneNumber {
    @Id
    @Column(name="phone_number")
    private String number;

    public PhoneNumber() {
    }

    public PhoneNumber(String phoneNumber) {
        this.number = phoneNumber;
    }


    public String getPhoneNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "PhoneNumber{" +
                ", phoneNumber='" + number + '\'' +
                '}';
    }


}
