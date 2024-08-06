package com.springboot.notificationsApp.redis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class RedisConfig {
    @Bean
    public Jedis jedis(@Value("${spring.data.redis.host}") final String host,
                       @Value("${spring.data.redis.port}") final int port) {
        return new Jedis(host, port);
    }
}
