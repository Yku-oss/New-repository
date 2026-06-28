//跨域配置类，配置CORS跨域访问规则，允许前端跨域请求API接口
//可以解决前端开发过程中遇到的跨域问题，使得前端能够正常访问后端API接口，使其顺畅不卡顿
package com.example.demo.config;

import org.springframework.context.annotation.Configuration; //这是导入spring的跨域注解类
import org.springframework.web.servlet.config.annotation.CorsRegistry; // 这是一个spring的注册表类，用于集中处理CORS的配置规则的方法
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // 这是一个spring MVC 的配置接口方法，servlet的作用就是处理HTTP请求的包
// 三个类的合起来的作用就是，能够创建spring的注解与定义接口，还可以定义CORS的配置规则。


@Configuration // 配置源注解，spring扫描后可以识别我们定义好的规则和接口以及配置。
public class CorsConfig implements WebMvcConfigurer { //定义一个公开的名为 CorsConfig 的方法，实现 WebMvcConfigurer接口
    // 也是告诉spring 这个类可以自定义MVC配置
    @Override // 重写父接口的方法
    public void addCorsMappings(CorsRegistry registry) { // 接受传入spring传入的注册表对象参数，用来注册跨域规则
        registry.addMapping("/api/**") // 向注册表中添加映射规则，匹配哪一些路径
                .allowedOriginPatterns("*") // 允许所有来源访问，如果有多个不同的限制可以添加更准确的路径限制，具体路径的优先级更高
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")// 配置允许的HTTP方法
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true); // 允许配带凭据
    }
}