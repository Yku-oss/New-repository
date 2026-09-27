package com.demo.configue;

import org.springframework.context.annotation.Configuration;//配置跨域注解方法
import org.springframework.web.servlet.config.annotation.CorsRegistry;// 跨域规则
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // 跨域接口

// 声明这是一个接口类
@Configuration
public class corsconfigue implements WebMvcConfigurer {
    @Override
    // 这是一个spring内部的方法，addCorsMappings 然后加上定义一个CorsRegistry参数
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS");
    }

}