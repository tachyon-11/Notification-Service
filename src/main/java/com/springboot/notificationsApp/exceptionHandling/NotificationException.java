package com.springboot.notificationsApp.exceptionHandling;

public class NotificationException extends RuntimeException {
    public NotificationException(String message) {
        super(message);
    }
}
