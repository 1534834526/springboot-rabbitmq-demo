package com.send.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SendApplication.class, args);
    }



}
