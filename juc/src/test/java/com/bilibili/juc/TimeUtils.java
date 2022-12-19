package com.bilibili.juc;



import java.time.Duration;
import java.time.LocalTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static void main(String[] args) {
        LocalTime startTime = LocalTime.of(5,30);
        LocalTime endTime = LocalTime.of(11,0);
        System.out.println(Duration.between(startTime, endTime).toMinutes());

        for (int i = 0; i < 1000; i++) {
            System.out.println(startTime.plusMinutes(new Random().nextInt(330)));
        }

    }
}
