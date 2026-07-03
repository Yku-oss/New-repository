//这是项目启动类，同时启动 Spring Boot 后端 和 JavaFX 桌面界面
package com.example.demo;

//引入javafx的包
import com.example.demo.ui.FxApplication; // 引入javafx的启动项类
import javafx.application.Application; // 再引入javafx的包让其他类也要继承javafx的启动
// 引入spring的包
import org.springframework.boot.SpringApplication; //spring的启动项
import org.springframework.boot.autoconfigure.SpringBootApplication; // 其他类也要继承spring的启动
import org.springframework.context.ConfigurableApplicationContext; //容器创建

// 声明 spring-boot 框架的启动
@SpringBootApplication
public class DemoApplication {

	private static ConfigurableApplicationContext context; 
	// 静态变量，用来存放bean的spring容器
	// 这是因为javafx 和 springboot 两个框架各有各自的流通，所以要存放共两个框架一起使用。
	public static void main(String[] args) {
		// 先启动 Spring Boot,将spring启动的bean存入到容器中
		// SpringApplication是一个引导类，他的作用是加载配置，扫描注解，创建所需的类并注入等
		// run() 方法是一个阻塞类，它能让程序一直运行直到关闭
		// DemoApplication.class 就是这个方法扫描这个类以及所有依附类
		// args 就是命令参数
		context = SpringApplication.run(DemoApplication.class, args);
		// 再启动 JavaFX 桌面界面（在独立线程中，因为 Application.launch 会阻塞）
		//创建一个线程来让javafx启动
		//() -> 是lamba表达式，匿名创建一个方法，让其更简洁
		// launch（）是一个Javafx启动方法
		new Thread(() -> Application.launch(FxApplication.class, args)).start();
	}
	// 创建一个方法让别的类都能从里面取出对象
	public static ConfigurableApplicationContext getContext() {
		return context;
	}
	
}
