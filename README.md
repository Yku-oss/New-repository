# 邮局订报管理系统

基于 Spring Boot + MyBatis + MySQL 的课程设计项目。

## 技术栈

| 技术 | 版本 | 作用 |
|------|------|------|
| Spring Boot | 3.4.0 | 后端框架 |
| MyBatis | 3.0.3 | 数据库操作 |
| MySQL | 8.0 | 数据存储 |

## 项目结构

````
src/main/java/com/example/demo/
├── DemoApplication.java # 启动类
├── controller/ # 控制层（接收请求）
├── service/ # 业务层（处理逻辑）
│ └── impl/ # 业务层实现
├── mapper/ # 数据访问层（执行 SQL）
└── entity/ # 实体类（映射表）
````

····

## 核心注解速查

| 注解 | 作用 |
|------|------|
| `@RestController` | 标记为控制器，返回 JSON |
| `@GetMapping` | 处理 GET 请求（查询） |
| `@PostMapping` | 处理 POST 请求（新增） |
| `@PutMapping` | 处理 PUT 请求（修改） |
| `@DeleteMapping` | 处理 DELETE 请求（删除） |
| `@PathVariable` | 从 URL 路径取值 |
| `@RequestBody` | 从请求体取 JSON 数据 |
| `@Autowired` | 自动注入依赖 |
| `@Service` | 标记为业务层组件 |
| `@Mapper` | 标记为 MyBatis 接口 |

## 请求执行流程（以 GET /api/newspapers 为例）

1. 浏览器发送 GET 请求
2. Controller 匹配路径，调用 Service
3. Service 调用 Mapper
4. Mapper 执行 SQL，从数据库取数据
5. 数据逐层返回，Spring 自动转成 JSON
6. 浏览器收到 JSON 响应

## 接口列表

| 模块 | 方法 | 接口 | 说明 |
|------|------|------|------|
| 报纸 | GET | `/api/newspapers` | 查所有 |
| 报纸 | GET | `/api/newspapers/{id}` | 查单个 |
| 报纸 | POST | `/api/newspapers` | 新增 |
| 报纸 | PUT | `/api/newspapers/{id}` | 修改 |
| 报纸 | DELETE | `/api/newspapers/{id}` | 删除 |
| 客户 | GET/POST/PUT/DELETE | `/api/customers` | 同理 |
| 订单 | GET/POST | `/api/subscriptions` | 查/增 |

## 遇到的问题及解决

| 问题 | 原因 | 解决 |
|------|------|------|
| 编译错误找不到类 | 文件没保存 | Ctrl+S 保存后再编译 |
| 404 接口不存在 | 没有对应的 Controller | 创建 Controller 并加注解 |
| 数据库连接失败 | 密码错误或 MySQL 没启动 | 检查密码，启动 MySQL |

## 如何运行

1. 创建 MySQL 数据库 `newspaper`
2. 执行 `sql/schema.sql` 建表
3. 修改 `application.properties` 中的数据库密码
4. 运行 `mvn spring-boot:run`
5. 访问 `http://localhost:8080/api/newspapers`
6. ····
