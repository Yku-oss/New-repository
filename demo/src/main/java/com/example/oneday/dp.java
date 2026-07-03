package com.example.oneday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class dp {
    private static ConfigurableApplicationContext ct;
    public static void main(String[] args) {
        ct = SpringApplication.run(dp.class, args);
    }

    public static ConfigurableApplicationContext getbean(){
        return ct;
    }
}
