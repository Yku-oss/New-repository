
## 类解析
这个类的作用是：将其他插件或者是框架与spring-boot连接起来 比如：spring + javafx，让其共享同一个bean（对象）

## 包引入
````
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
````
这些是spring 与 javafx的包与方法，也相当于导入接口

## 相关知识解释
````
@Component ：这是注解，是spring的一个包，作用是向 Spring IoC 容器声明，启动spring时自动创建，需要管理。
@Override ：是java语法，声明这是一个重构或者父亲类
ApplicationContextAware ：这是一个感应spring的接口，向后续getbean传递信息。
ApplicationContext ：这是spring中的顶层接口，负责管理bean。
setApplicationContext ：这是一个接受和保存spring的一个方法，将方法存在端口中，通常只执行一次
getBean：是一个将bean从spring中取出的方法，他能被其他类随时调用。

````