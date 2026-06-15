//这是项目启动类，包含main方法，是Spring Boot应用的入口点。
//相当于Java应用的主类，负责启动整个Spring Boot应用程序。
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
