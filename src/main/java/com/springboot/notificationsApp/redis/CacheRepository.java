package com.springboot.notificationsApp.redis;

public interface CacheRepository {
    void save(String PhoneNumber);
    void delete(String PhoneNumber);
    boolean exists(String PhoneNumber);
}
