
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

## 流程图
````
         spring启动
            ⬇
  同时创建ApplicationContext接口容器
            ⬇
  spring主动扫描类然后创建并实现了Applicationware方法
            ⬇
  当前端和其他插件有bean需求时
            ⬇
  Applicationware就会感应到，就是spring扫描到有需求
            ⬇
  spring会主动将接口存入setApplication中
            ⬇
  spring继续扫描然后等待其他bean
            ⬇
  然后将bean存入容器就是Application接口中
            ⬇
  getbean方法就会从spring接口中提取出bean返回到需求类


