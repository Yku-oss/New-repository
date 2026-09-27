# Spring 原理总结（Java 后端面试）

> 复习法：每个考点背「一句话结论」+「追问链 2-3 层」+「项目里怎么用它」。
> 学习记录：2026-08-23 ~ 08-27（IoC → Bean → AOP → 事务 → 循环依赖 → 自动装配 → 失效/幂等）

---

## 一、IoC 容器（控制反转）

**一句话**：把"对象的创建和依赖管理"的控制权，从程序员自己 `new` 反转给 Spring 容器。

```java
// 以前：自己 new，耦合高
private UserService userService = new UserService();
// 现在：@Autowired 注入，由容器管理
@Autowired
private UserService userService;
```

- **IoC** = 一种设计**思想**（控制权反转）
- **DI（依赖注入）** = 实现 IoC 的**方式**（容器把依赖注入对象）

**好处（必答）**：解耦 / 方便替换实现（提高可维护性和扩展性） / 容易测试（注入 Mock）/ 容器统一管理 Bean 生命周期（资源的统一管理）。

**记忆锚**：> "控制反转 = 对象和依赖交给容器管；DI 注入是实现方式；好处是解耦 + 好维护。"

---

## 二、Bean 生命周期

**核心 5 阶段（记忆锚）**：
```
实例化 → 属性填充 → 初始化 → 使用 → 销毁
①实例化：new 出对象（有内存）
②属性填充：@Autowired 注入依赖
③初始化：@PostConstruct / InitializingBean.afterPropertiesSet / init-method
④使用
⑤销毁：@PreDestroy / DisposableBean.destroy
```

**初始化阶段的三个钩子（按执行顺序）**：
1. `@PostConstruct`（注解，最常用）
2. `InitializingBean.afterPropertiesSet()`（实现接口，方法名="属性设置后"）
3. 自定义 `init-method`（`@Bean(initMethod="...")`）
> 顺序：注解 → 接口 → 配置的 init-method。

**关键点**：
- **实例化 ≠ 初始化**：实例化 = new 出对象（有内存）；初始化 = 属性填完后的自定义逻辑。
- **属性填充 先于 初始化**：因为初始化钩子（如 `@PostConstruct`）里要用 `@Autowired` 注入的依赖；顺序反了依赖还没注入 → NPE。
- **AOP 代理在哪生成？** → **初始化之后**（`BeanPostProcessor.postProcessAfterInitialization`）

**类比**：Bean 像新员工入职——实例化(招进来) → 填充(配工位) → 初始化(培训) → 使用(干活) → 销毁(离职交接)

---

## 三、AOP 动态代理（JDK vs CGLIB）

**AOP**：把通用逻辑（日志/事务/权限）横切注入，通过"代理对象"在方法前后插入增强。

| | JDK 动态代理 | CGLIB 代理 |
|---|---|---|
| 基于 | **接口** | **子类** |
| 前提 | 目标必须实现接口 | 不需要接口 |
| 局限 | 无接口用不了 | **final 类/方法**代理不了 |

**Spring 怎么选（必考）**：
- 传统 Spring / Boot 1.x：有接口 → **JDK**；无接口 → CGLIB
- **Spring Boot 2.x+：默认强制 CGLIB**（即使有接口也用 CGLIB）

**记忆锚**：> "JDK 靠接口，CGLIB 靠子类；有接口→JDK，无接口→CGLIB；final 都不能代理；Boot2.x 默认强制 CGLIB。"

`@Transactional` 就是靠 AOP 代理实现的：方法被代理拦截 → 方法前开启事务、方法后提交/回滚。

---

## 四、@Transactional 事务

### 4.1 事务回滚（rollback）

- **提交（commit）** = 确认永久生效
- **回滚（rollback）** = 撤销，当作没发生

**为什么需要**：保证一致性——一组操作要么**全成功（提交）**，要么**全不发生（回滚）**。

秒杀例子：`doSeckill` 里 `@Transactional` 包了两步（扣库存 + 落单）。如果落单失败（如一人一单冲突抛异常）→ **整个事务回滚** → 扣的库存也撤销 → 不会出现"库存扣了但没订单"。

### 4.2 事务传播行为（propagation）

| 级别 | 行为 | 场景 |
|---|---|---|
| **REQUIRED**（默认）| 有事务就加入，没有就新建 | 绝大多数业务 |
| **REQUIRES_NEW** | 永远新开独立事务（挂起当前）| 内层独立提交/回滚，不受外层影响 |
| **NESTED** | 嵌套事务（保存点）| 内层失败只回滚内层 |

**记忆锚**：> "REQUIRED = 有就加入（默认）；REQUIRES_NEW = 永远新开独立；NESTED = 嵌套保存点。"

### 4.3 事务失效场景（面试必考）

**① 同类内部调用（this.B()）**：`this.B()` 直接调目标方法，**绕过 AOP 代理** → 事务切面没拦截 → 失效。
```java
class Service {
  public void A() { this.B(); }      // 直接调 this，没走代理 → B 的事务失效！
  @Transactional
  public void B() { ... }
}
```
**解决**：①拆到另一个 Service 调用 ②注入自身代理 ③`AopContext.currentProxy()`

**② private / final 方法**：代理（CGLIB 子类）没法重写 private/final → 切面不生效。改成 public。

**③ 异常被吞**：事务靠"异常往外抛"触发回滚。如果 catch 住异常**没再抛** → 事务以为正常完成就提交了。
```java
@Transactional
public void f() {
  try { ... } catch (Exception e) { log.error(e); }  // 吞异常 → 不回滚！
}
```
解决：catch 后重新 throw，或用 `@Transactional(rollbackFor = Exception.class)`（默认只回滚 RuntimeException）。

**核心排查套路**：
```
遇到"该回滚没回滚" →
① 是不是同类内部调用（没走代理）？
② 是不是 private/final（代理不了）？
③ 是不是异常被吞了（没往外抛）？
④ 是不是方法根本没被容器管理（没成 Bean）？
```

---

## 五、循环依赖（三级缓存）

**问题**：A 依赖 B、B 依赖 A → 死循环。用**三级缓存**解决。

| 级别 | 名字 | 存什么 |
|---|---|---|
| 一级 | `singletonObjects` | 成品 Bean |
| 二级 | `earlySingletonObjects` | **早期/半成品** Bean |
| 三级 | `singletonFactories` | Bean 工厂（生成早期引用）|

**流程（A↔B）**：new A → 放三级缓存 → A 要 B → new B → B 要 A → 从三级缓存工厂拿 A 早期引用 → B 完成进一级 → 回 A 完成进一级。

**必考细节**：
1. **为什么三级不是二级**：三级存**工厂**，为了**延迟生成 AOP 代理**（避免代理创建两次）
2. **构造器注入解决不了**：对象还没 new 出来，无法提前暴露半成品。只有 **setter/属性注入**能解决
3. 只有**单例**支持，原型（prototype）不支持

**类比**：A、B 互相需要。A 先给"临时占位/联系方式"（三级缓存工厂），B 拿着先用，等 B 搞定 A 再补全正式确认。

**记忆锚**：> "三级缓存：一级成品、二级半成品、三级工厂；提前暴露半成品；不支持构造器注入、不支持原型。"

---

## 六、Spring Boot 自动装配（AutoConfiguration）

**一句话**：启动时根据依赖自动配好组件（数据源、Redis、Web 容器），不用手动写一堆配置。

**核心：`@SpringBootApplication` 三合一**：
```java
@SpringBootApplication =
  @SpringBootConfiguration  // 配置类
+ @EnableAutoConfiguration  // 开启自动装配
+ @ComponentScan           // 扫描组件
```

**原理三步**：
1. **读清单**：`@EnableAutoConfiguration` 读 `META-INF/spring/...AutoConfiguration.imports`（所有 XxxAutoConfiguration 的清单）
2. **条件装配**：每个自动配置类带 `@ConditionalOnClass` 等条件，**满足才生效**（如加了 Redis 依赖 → RedisTemplate 类存在 → 配 Redis）
3. **注入默认 Bean**：条件成立后容器自动创建对应 Bean

**如何覆盖**：自动配置是默认的，**自定义 Bean 优先级更高**（`@Bean` 定义同名 Bean 覆盖 / 改 application.yml 属性 / `@ConditionalOnMissingBean`）。

**记忆锚**：> "@SpringBootApplication 三合一；自动配置 = 读 imports 清单 + @ConditionalOnClass 条件判定 + 注入默认 Bean；自定义 Bean 优先级更高可覆盖。"

---

## 七、接口幂等与重试（分布式关键）

**问题**：创建订单接口超时，为什么不能无条件重试？

**原因**：创建订单**不是幂等**操作。**超时 ≠ 失败**——可能其实成功了只是响应没回来。盲目重试 → **重复下单 / 重复扣款**。

**正确做法（幂等设计）**：
1. **客户端带唯一请求号**（幂等键）：同请求号只处理一次，重复请求返回第一次结果
2. **服务端唯一约束兜底**：如订单号 `uk_order_no` 唯一键，重复插入直接报错/返回已有订单
3. **先查再重试**：重试前先查"这笔是否已存在"

**秒杀项目**：`order.setOrderNo(UUID...)` + `uk_order_no` 唯一键 = 幂等实现。

**记忆锚**：> "幂等 = 同一操作重复执行结果一致。接口超时别无条件重试，用唯一键/幂等号/先查再试。"

---

## 八、自查清单

```
[ ] 能说出 IoC 和 DI 的关系 + IoC 4 个好处
[ ] 能背出 Bean 生命周期 5 阶段 + AOP 代理在初始化后生成
[ ] 能说出 JDK vs CGLIB 区别 + Spring Boot 默认强制 CGLIB
[ ] 能说出事务失效 3 大场景（同类调用/private/吞异常）+ 排查套路
[ ] 能说出 REQUIRED vs REQUIRES_NEW
[ ] 能说出三级缓存 + 为什么三级不是二级 + 构造器注入为何不行
[ ] 能说出自动装配原理（读清单 + @Conditional + 覆盖）
[ ] 能说出接口为什么不无条件重试 + 幂等设计
```
