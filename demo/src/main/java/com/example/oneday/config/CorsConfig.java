package com.example.oneday.config;

import org.springframework.context.annotation.Configuration; // 引入跨域注解配置
import org.springframework.web.servlet.config.annotation.CorsRegistry; // 跨域注册器，制定跨域规则
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; //web MVC方法，指定跨域接口


@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedHeaders("*")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
    
}