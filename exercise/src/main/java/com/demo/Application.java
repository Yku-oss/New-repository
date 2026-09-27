package com.demo;

import org.springframework.boot.SpringApplication;//框架启动包
import org.springframework.boot.autoconfigure.SpringBootApplication;//自动注入的工具启动包
import org.springframework.context.ConfigurableApplicationContext;// 容器启动包

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        // run方法启动包
        SpringApplication.run(Application.class, args);
    }
    
}