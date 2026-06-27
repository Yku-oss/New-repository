package com.example.demo;

import com.example.demo.ui.FxApplication;
import javafx.application.Application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class dpcation {
    // 创建静态变量来储存bean
    private static ConfigurableApplicationContext ct;
    
    public static void main(String[] args) {
        // 先启动spring,用容器储存spring创建的bean
        ct = SpringApplication.run(dpcation.class, args);
        // 创建一个新线程来启动javaFx
        new Thread(() -> Application.launch(FxApplication.class,args)).start();  
    }

    
}