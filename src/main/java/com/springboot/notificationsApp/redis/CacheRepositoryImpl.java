package com.springboot.notificationsApp.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

@Repository
public class CacheRepositoryImpl implements CacheRepository {
    private final Jedis jedis;
    @Value("${spring.cache.redis.time-to-live}")
    private Integer TIMEOUT;

    public CacheRepositoryImpl(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public void save(String phoneNumber) {
        jedis.set(phoneNumber, "True");
        jedis.expire(phoneNumber, TIMEOUT);
    }

    @Override
    public void delete(String phoneNumber) {
        jedis.del(phoneNumber);
    }

    @Override
    public boolean exists(String phoneNumber)  {
        return jedis.exists(phoneNumber);
    }

}
