## 为什么要配置环境
```
    因为当springboot启动的时候会先读取配置文件的内容，springboot会根据配置文件的内容来决定加入哪些依赖，加载哪些配置类，就比如怎么连接sql，连接哪一个sql，怎么去连接。如果我们不配置好，那么springboot就会按默认值处理，导致启动失败。
```

## 其包含以下内容

### SSE 协议
```
  SSE(Server-Sent Events) 是一个客户端和服务器之间的加密连接通道，确保信息是安全的，

  1.打招呼”与“回应”：你的浏览器向服务器发送一个“ClientHello”消息，包含自己支持的SSL/TLS版本和加密算法列表。服务器从中选择合适的选项，并以“ServerHello”消息回应。

  2.服务器出示“身份证”：服务器会向浏览器发送其数字证书。这个证书由受信任的第三方机构（CA）颁发，相当于服务器的“身份证”，包含了服务器的公钥和身份信息。

  3.浏览器验证身份：浏览器会验证证书的合法性，包括是否由受信任的CA签发、是否过期、域名是否匹配等。验证失败浏览器会发出警告。

  4.交换“会话密钥”：验证通过后，浏览器生成一个预主密钥（Pre-Master Secret），用服务器的公钥加密后发送给服务器。

  5.生成主密钥并确认：服务器用自己的私钥解密得到预主密钥。随后，双方根据这个预主密钥以及之前交换的随机数，独立计算出用于后续通信的主密钥（Master Secret），并互相发送“Finished”消息确认一切就绪。

  6.开始加密通信：握手完成。从现在开始，所有应用数据的传输都将使用协商好的会话密钥进行对称加密，安全通道就此建立。

  总结就是，当你连接https的时候，浏览器和服务器之间会进行一系列的握手和验证过程，确保双方身份的真实性，并协商出一个安全的加密通道，用于后续的数据传输。
```

### url 数据库的连接地址
```
    构成 ：
    jdbc:mysql://localhost:3306/demo_db?参数1&参数2&参数3
                    │        │      │     │
                    │        │      │     └── 参数（可选，但建议都加上）
                    │        │      └───────── 数据库名
                    │        └──────────────── 端口号（MySQL 默认 3306）
                    └──────────────────────────── 主机地址（localhost = 本机）
    比如 ： jdbc:mysql://localhost:3306/newspaper?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&allowPublicKeyRetrieval=true

useSSL=false ： 禁用 SSL 连接，避免证书验证问题。一般在本地开发是禁止的，在生产环境中是建议开启的。

serverTimezone=Asia/Shanghai ： 设置服务器时区，避免时区不一致导致的时间问题。

characterEncoding=utf-8 ： 设置字符编码为 UTF-8，确保中文等字符正常显示。

allowPublicKeyRetrieval=true ： 允许客户端从服务器检索公钥，解决某些情况下的连接问题，尤其是在使用新版本的 MySQL 驱动时。
```

### driver-class-name —— 数据库驱动
```
    作用就是：指定数据库驱动的类名，Spring Boot 会根据这个类名来加载相应的数据库驱动。
            MySQL 8.0 及以上版本用 com.mysql.cj.jdbc.Driver；MySQL 5.x 用 com.mysql.jdbc.Driver

    总结就是 ： 告诉springboot用哪一个驱动去连接数据库。
```

### 用户名 与 密码
```
    username ： 数据库的用户名，用于身份验证，确保只有授权用户才能访问数据库。
    password ： 数据库的密码，与用户名配合使用，进一步确保安全性。
```

### 连接池配置（HikariCP）
```
    连接池就是一个java与数据库的连接容器，里面装着很多连接对象，当需要很多连接的时候，可以直接从连接池里拿来使用，而不是每次都去创建一个新的连接对象，这样可以提高效率，减少资源消耗。
    当客户端发送请求数据库时，连接池就会快速抽出一个接口连接对象，直到使用完毕后就返回给连接池
    hikari:
        maximum-pool-size: 10 --最大连接数
        minimum-idle: 5 -- 最小空闲连接数
        connection-timeout: 30000 --连接超时最长等待时间（ms）
        idle-timeout: 600000 --连接最大空闲时间（ms） 空闲超过这个时间则就会被回收重建
        max-lifetime: 1800000 --连接最大存活时间（ms），超过这个时间则就会被回收重建

    为什么要连接：
    1.提高业务性能。每次数据库创建时连接都很耗时。（TCP/SSL 握手和认证）
    2.连接池提前建立好连接，用的时候直接从连接池里拿，使用完毕后归还给连接池
    3.连接池可以管理连接的生命周期，避免连接泄漏和资源浪费。
```

### Mybatis数据库框架
```
    MyBatis 是一个持久层的框架，它通过 XML 或注解的方式将sql语句和java解耦，简化了数据库的操作，然后映射成java对象返回给Java程序使用。MyBatis 主要用于简化数据库操作，提供了更灵活的 SQL 映射和查询功能。
   
    mybatis:
        mapper-locations: classpath:mapper/*.xml
        type-aliases-package: com.example.demo.entity
        configuration:
                map-underscore-to-camel-case: true
                log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

    mapper-locations ：指定 MyBatis 的映射文件（Mapper XML 文件）的位置，Spring Boot 会根据这个路径加载对应的 Mapper 文件。
    
    type-aliases-package ：指定 MyBatis 的类型别名包（entity），Spring Boot 会扫描这个包下的类，并为它们创建别名，简化 SQL 映射文件中的类型引用。
    
    map-underscore-to-camel-case ：启用下划线转驼峰命名规则，将数据库中的下划线命名（如 user_name）自动映射为 Java 对象的驼峰命名（如 userName）。
    
    log-impl ：指定 MyBatis 的日志实现类，这里使用的是标准输出日志，就是打开sql到控制台，方便调试
```
### JDBC(Java Database Connectivity) 数据库连接
```
    JDBC 是 Java 提供的一套用于连接和操作数据库的API，定义了一组标准的接口，用于让java程序和各种关系类型的数据库进行交互（比如 MySQL、PostgreSQL 等）

    核心来说就是 JDBC 充当了Java 和 数据库之间的桥梁，提供了统一的Java代码进行操作数据库。
    
    JDBC 也是MyBatis的底层实现，MyBatis 通过 JDBC 来执行 SQL 语句和处理结果集。单单是JDBC代码的实现，会显得代码特别冗长，MyBatis 就是对JDBC的封装，简化了数据库操作。
```



### 日志输出
```
    logging:
        level:
            root: info
            com.example.demo: debug

    root ：设置全局日志级别，info 表示输出信息级别及以上的日志（如警告、错误等）。
    
    com.example.demo ：指定特定包的日志级别，这里设置为 debug，表示输出调试级别及以上的日志，方便开发和调试。
```