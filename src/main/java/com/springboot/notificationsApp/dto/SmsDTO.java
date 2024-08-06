package com.springboot.notificationsApp.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;


public class SmsDTO {
    @NotBlank(message = "Please enter Phone Number")
    @Pattern(message = "Please enter a valid number",regexp = "\\d{10}")
    String phoneNumber;
    @NotBlank(message = "Please enter message")
    String message;

    public SmsDTO(String phoneNumber, String text) {
        this.phoneNumber = phoneNumber;
        this.message = text;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMessage() {
        return message;
    }

}
