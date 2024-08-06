package com.springboot.notificationsApp;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
@RequiredArgsConstructor
public class NotificationsAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(NotificationsAppApplication.class, args);
	}

}
