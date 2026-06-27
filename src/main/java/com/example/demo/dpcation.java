package com.example.demo;

import com.example.demo.ui.FxApplication;
import javafx.application.Application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class dpcation {
    private static ConfigurableApplicationContext ct;
    public static void main(String[] args) {
        ct = SpringApplication.run(dpcation.class, args);
        new Thread(() -> Application.launch(FxApplication.class,args)).start();
    }

    public static ConfigurableApplicationContext getct(){
        return ct;
    }
}