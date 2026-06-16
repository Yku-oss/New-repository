# Spring Boot + MyBatis 知识点整理

## 1. 项目分层结构
````
Controller → Service → Mapper → Database
↓              ↓       ↓           ↓
接收请求    业务逻辑  执行SQL    连接数据库
````

## 2. 核心注解

### 控制器层
- `@RestController`：标记为控制器，返回 JSON
- `@RequestMapping`：设置路径前缀
- `@GetMapping`：处理 GET 请求（查）
- `@PostMapping`：处理 POST 请求（增）
- `@PutMapping`：处理 PUT 请求（改）
- `@DeleteMapping`：处理 DELETE 请求（删）
- `@PathVariable`：从 URL 路径取值，如 `/api/1` 取到 1
- `@RequestBody`：从请求体取 JSON，转成 Java 对象

### 依赖注入
- `@Autowired`：让 Spring 自动注入依赖的对象

### 业务层
- `@Service`：标记为业务层组件

### 数据访问层
- `@Mapper`：标记为 MyBatis 接口

### MyBatis 注解
- `@Select("SQL")`：查询
- `@Insert("SQL")`：插入
- `@Update("SQL")`：更新
- `@Delete("SQL")`：删除
- `@Param("name")`：给 SQL 参数命名
- `@Options(useGeneratedKeys=true, keyProperty="id")`：获取自动生成的主键

### Java 注解
- `@Override`：告诉编译器这是覆盖/实现方法

## 3. HTTP 方法对应操作

| 方法 | 操作 | 例子 |
|------|------|------|
| GET | 查询 | `GET /api/newspapers` |
| POST | 新增 | `POST /api/newspapers` |
| PUT | 修改 | `PUT /api/newspapers/1` |
| DELETE | 删除 | `DELETE /api/newspapers/1` |

## 4. 请求-响应流程

````
浏览器 → Controller → Service → Mapper → 数据库
(接收) (逻辑) (SQL)
↓ ↓ ↓
返回JSON ← 返回数据 ← 返回数据
````


## 5. SQL 示例

### 查询所有报纸
```sql
SELECT * FROM newspaper;
```
### 查询单个报纸
```sql
SELECT * FROM newspaper WHERE id = 1;
```
查询订单详情（带客户名和报纸名）
```sql
SELECT s.*, c.name, n.name
FROM subscription s
JOIN customer c ON s.customer_id = c.id
JOIN newspaper n ON s.newspaper_id = n.id
WHERE s.customer_id = 1;
```

## 6. 常用 Git 命令
```
命令	     作用
git status	查看改动
git add .	添加所有改动
git commit -m "说明"	提交到本地
git push	推送到 GitHub
```