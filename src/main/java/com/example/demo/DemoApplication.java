//这是项目启动类，同时启动 Spring Boot 后端 和 JavaFX 桌面界面
package com.example.demo;

//引入javafx的包
import com.example.demo.ui.FxApplication;
import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) {
		// 先启动 Spring Boot
		SpringApplication.run(DemoApplication.class, args);
		// 再启动 JavaFX 桌面界面（在独立线程中，因为 Application.launch 会阻塞）
		new Thread(() -> Application.launch(FxApplication.class, args)).start();
	}
	
}
