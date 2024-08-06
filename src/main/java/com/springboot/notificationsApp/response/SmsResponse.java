package com.springboot.notificationsApp.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SmsResponse {
    private final String message;
    private final String Id;
    private final LocalDateTime timestamp;

    public SmsResponse(String message, String id, LocalDateTime timestamp) {
        this.message = message;
        Id = id;
        this.timestamp = timestamp;
    }


}
