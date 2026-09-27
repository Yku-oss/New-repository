package com.example.oneday;

import org.springframework.boot.SpringApplication; //Springboot工具的启动类
import org.springframework.boot.autoconfigure.SpringBootApplication;//自动注入的启动包注解
import org.springframework.context.ConfigurableApplicationContext;//存放公用bean的容器

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
