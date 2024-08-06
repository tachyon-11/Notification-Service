package com.springboot.notificationsApp.thirdpartyapi;
import java.util.Random;

public class RandomNumberGenerator {
    public static int getRandomNumber() {
        Random random = new Random();
        return random.nextInt(10); // Generates a random number between 0 (inclusive) and 10 (exclusive)
    }
}
