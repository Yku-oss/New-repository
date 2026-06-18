package com.example.demo.config;
//这是一个工具类，是能够将其他插件与框架与springboot进行结合的工具类
// 主要的功能是让非spring框架管理的类进行在spring容器中获取bean对象
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 容器持有者 - 让非 Spring Bean（如 JavaFX 界面）也能获取到 Service
 */

// Component 注解表示这个类是一个 Spring 组件，会被 Spring 扫描并管理
// 作用是让 Spring 在启动时创建这个类的实例，并将其放入 Spring 容器中，
// 也就是先启动springboot后会扫描整个模块，发现注解后会创建这个类的实例，
// 这样其他地方就可以通过这个类来获取bean对象了
@Component
public class SpringContextHolder implements ApplicationContextAware {
    // ApplicationContext 是spring的顶层接口，管理着bean，还能读取bean的信息
    private static ApplicationContext context;

    // Override 注解表示这个方法是实现了 ApplicationContextAware 接口中的方法
    // 就是一个父亲类或者接口定义的方法。
    @Override
    // 这个方法是在springboot回调后收到spring容器的上下类信息后调用的，
    // 就是获取spring中bean的上下文信息，并将其赋值给静态变量context，
    // 这样就可以在其他地方通过这个类来获取bean对象了，这个方法其他类只要引用都可以使用。
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }                                                                                                

    // 这是一个获取对象的静态方法，可以从spring容器中获取任何类型的bean对象
    // 通过传入一个Class对象，来指定要获取的bean的类型
    // 只要import这个类，就可以在任何文件中或者这个方法来得到bean对象
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }
}


