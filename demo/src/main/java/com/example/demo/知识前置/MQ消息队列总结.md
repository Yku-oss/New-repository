# MQ 消息队列知识总结

## 一、MQ 是什么？为什么用？

MQ（Message Queue，消息队列）是一种**异步通信**的中间件。生产者把消息发到队列，消费者从队列取消息处理，生产者和消费者**解耦**。

### 三大核心作用

```
    ① 异步解耦
        同步调用：下单 → 等短信 → 等积分 → 等日志（串行，慢）
        异步调用：下单 → 发消息到 MQ → 立即返回（快）
                 短信服务、积分服务自己从 MQ 消费（并行）

    ② 削峰填谷
        秒杀瞬间 10 万请求 → 直接打到 DB 会打崩
        先到 MQ 排队 → 消费者按自己的速度慢慢处理
        → 保护下游系统

    ③ 数据分发
        一个消息被多个系统消费（订单系统、库存系统、报表系统都关心下单）
``` 

### 引入 MQ 的代价
```
    · 系统复杂度上升（链路变长）
    · 可用性下降（MQ 挂了怎么办 → 高可用集群）
    · 一致性问题（消息丢失/重复/顺序）
    · 运维成本增加
```

---

## 二、主流 MQ 对比

| 特性 | RocketMQ | Kafka | RabbitMQ |
|------|---------|-------|---------|
| 开发方 | 阿里 | Apache/LinkedIn | Pivotal |
| 语言 | Java | Scala/Java | Erlang |
| 吞吐量 | 10 万级 | 百万级（最高） | 万级 |
| 消息可靠性 | 高 | 中（副本可补） | 高 |
| 事务消息 | ✅ 原生支持 | ✅ 2.5+ | ❌ |
| 延时消息 | ✅ 原生支持 | ❌ 需自实现 | 用死信+TTL 模拟 |
| 死信队列 | ✅ | ❌ | ✅ |
| 消息顺序 | ✅ 分区有序 | ✅ 分区有序 | ✅ 单队列有序 |
| 消费模型 | 推 | 拉 | 推/拉 |
| 适用场景 | 业务消息（订单/交易） | 日志/埋点/大数据 | 中小规模业务 |

### 选型建议
```
    · 业务消息（订单、交易、通知）→ RocketMQ（功能全、可靠性高）
    · 日志/埋点/大数据流 → Kafka（吞吐量天花板）
    · 简单场景、团队不熟 Java → RabbitMQ（简单易用）
    · 阿里系生态 → RocketMQ
```

---

## 三、核心概念

### 通用概念
```
    Producer    生产者：发送消息的一方
    Consumer    消费者：接收并处理消息的一方
    Broker      消息服务器：存储和转发消息
    Topic       主题：消息的分类（类似数据库的表）
    Message     消息：传递的数据（body + 属性 + 唯一 id）
```

### RocketMQ 特有概念
```
    · NameServer：注册中心，管理 Broker 地址（类似 ZooKeeper）
    · Broker：消息存储节点（主从架构）
    · Queue：Topic 下的队列（一个 Topic 多个 Queue，并行能力来源）
    · ConsumerGroup：消费组（组内竞争消费，组间广播消费）
    · Tag：消息标签（细粒度过滤）
```

### Kafka 特有概念
```
    · Partition：分区（Topic 下的分片，并行单位）
    · Offset：偏移量（消费者消费位置）
    · ConsumerGroup：消费组（组内一个分区只能被一个成员消费）
    · Replica：副本（Leader + Follower）
    · ISR：同步副本集合（和 Leader 保持同步的副本）
```

### 消费模式
```
    · 集群消费（默认）：一个消息只被组内【一个】消费者处理
      场景：订单处理（一个订单只处理一次）
    · 广播消费：消息被组内【每个】消费者都处理
      场景：配置更新、全量缓存刷新
```

---

## 四、消息可靠性（三大必问之一）

### 消息丢失的三个环节

```
    生产环节（Producer → Broker）
        ↓
    存储环节（Broker 存储）
        ↓
    消费环节（Broker → Consumer）
```

### ① 生产者丢失消息
```
    原因：消息发出去，Broker 没收到或没确认

    解决方案：
        · 方案一：同步发送 + 确认机制
            RocketMQ：send 后返回 SendResult，检查 status
            Kafka：acks=all（等所有副本确认才算成功）
        · 方案二：失败重试
            发送失败自动重试（配置重试次数）
        · 方案三：事务消息（RocketMQ）
            本地事务成功才发消息，保证"业务 + 消息"一致

    最佳实践：
        Producer 开启 confirm 确认 + 失败重试
        极端情况把失败消息记录到本地表，定时补偿发送
```

### ② Broker 存储丢失
```
    原因：消息到了 Broker 但没持久化，宕机就丢了

    解决方案：
        · RocketMQ：开启同步刷盘（同步刷盘 > 异步刷盘）
            同步刷盘：写盘成功才返回 ack（可靠，慢）
            异步刷盘：先写内存，定期刷盘（快，可能丢）
        · Kafka：副本机制
            acks=all + min.insync.replicas=2
            消息写 Leader 并同步到 ISR 副本才算成功
        · 持久化配置：
            队列/主题开启持久化（durable）
```

### ③ 消费者丢失消息
```
    原因：消费完没确认（ack），或者还没处理完就 ack

    错误示例：
        · 自动 ack：Broker 发出消息就当消费成功
          → 消费者处理到一半挂了，消息已 ack，丢了

    解决方案：
        · 关闭自动 ack，处理成功后再手动 ack
        · RocketMQ：手动 ACK（ConsumeConcurrentlyStatus.CONSUME_SUCCESS）
        · Kafka：enable.auto.commit=false + 处理完手动 commitSync

    代码示例（RocketMQ 手动 ack）：
        // 关闭自动提交
        consumer.setConsumeFromWhere(...);
        consumer.registerMessageListener((MessageListenerConcurrently)
            (msgs, context) -> {
                try {
                    process(msgs);          // 业务处理
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;  // 成功 ack
                } catch (Exception e) {
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;  // 失败重试
                }
            });
```

### 消息可靠性总结
| 环节 | 丢失原因 | 兜底方案 |
|------|---------|---------|
| 生产者 | 发送失败 | confirm 确认 + 重试 + 事务消息 |
| Broker | 未持久化 | 同步刷盘 + 副本机制（acks=all） |
| 消费者 | 提前 ack | 手动 ack，处理成功才确认 |

---

## 五、消息重复消费与幂等性（三大必问之二）

### 为什么会重复？
```
    · 消费者处理成功，但 ack 发送时网络抖动丢了
    · Broker 没收到 ack → 重新投递
    · 消费者处理超时，Broker 认为失败重投

    → 网络不可靠，重复消费【无法完全避免】
    → 只能让消费者【幂等】：同一条消息处理多次结果一致
```

### 幂等方案

```
    · 方案一：唯一业务 ID + 数据库唯一约束（最可靠）
        每条消息带唯一业务 ID（如订单号）
        数据库表对该 ID 建唯一索引
        重复插入报错 → 说明已处理过，直接忽略

    · 方案二：Redis SETNX 去重
        处理前 SETNX msg_id 1（存在说明处理过）
        处理完删除（或带过期时间）

    · 方案三：状态机校验
        订单状态：已支付 → 已发货
        重复消息进来，判断状态已变更就忽略

    · 方案四：数据库乐观锁 / 版本号
        UPDATE ... WHERE version = 旧版本
```

### 最佳实践
```
    生产端：生成全局唯一消息 ID（雪花算法）
    消费端：业务表唯一索引 + 状态判断双保险
```

---

## 六、消息积压（三大必问之三）

### 为什么积压？
```
    · 消费者处理慢（DB 慢、业务重）
    · 消费者挂了没恢复
    · 生产者瞬时洪峰（秒杀）
    · 消费者数量 < 队列/分区数量
```

### 处理思路

```
    第一步：先恢复消费能力（止血）
        · 重启挂掉的消费者
        · 增加消费者实例（水平扩容）
          ⚠️ 注意：消费者数不能超过队列/分区数，否则多出来的闲置

    第二步：临时转存（兜底，不丢业务）
        · 消息积压严重时，先把 MQ 里的消息导出/转存到临时 Topic
        · 或直接存到数据库/文件，后面慢慢补

    第三步：优化消费逻辑（治本）
        · 批量消费（一次拉多条）
        · 合并 DB 操作（批量 insert）
        · 异步化内部流程
        · 无用的逻辑从消费链路去掉

    第四步：补齐积压
        · 用新的消费者 + 新的消费组从临时存储重新消费
        · 或加大消费者并发数追数据
```

### 防止积压的预防措施
```
    · 消费能力监控：消费 lag（堆积数）告警
    · RocketMQ：查看消费进度 consume queue 的 offset 差
    · Kafka：kafka-consumer-groups --describe 看 LAG
    · 消费者设置合理的并发数和批量大小
```

---

## 七、消息顺序性

### 为什么需要顺序？
```
    业务场景：订单状态流转
        已下单 → 已支付 → 已发货 → 已完成
        如果乱序：已支付 先到，已下单 后到 → 状态回退，业务错误
```

### 全局有序 vs 局部有序
```
    · 全局有序：所有消息严格按顺序（吞吐极低，几乎不用）
    · 局部有序：同一业务 key 的消息有序（主流方案）
      如：同一个订单的消息有序，不同订单可以并行
```

### 实现方案
```
    · 生产者：同一业务 key（如订单号）路由到同一个队列/分区
      RocketMQ：MessageQueueSelector 按 key 选 queue
      Kafka：key 相同的消息进同一个 partition（默认 key hash 分区）

    · 消费者：该队列/分区【单线程】消费
      一个队列一个消费者线程，保证顺序

    代码（RocketMQ 顺序消息）：
        // 生产：按订单号选队列
        SendResult result = producer.send(msg, new MessageQueueSelector() {
            @Override
            public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                Long orderId = (Long) arg;
                return mqs.get(orderId.intValue() % mqs.size());  // 同一订单进同一队列
            }
        }, orderId);

        // 消费：用 MessageListenerOrderly（顺序消费）
        consumer.registerMessageListener((MessageListenerOrderly)
            (msgs, context) -> {
                process(msgs);   // 单线程按顺序处理
                return ConsumeOrderlyStatus.SUCCESS;
            });
```

### 注意事项
```
    · 消费失败重试也会影响顺序（重试的消息会排到后面）
    · 顺序消息的吞吐受限于单个队列/分区的单线程消费
```

---

## 八、事务消息（RocketMQ）

### 解决什么问题？
```
    场景：下单成功 → 必须发一条"扣库存"消息
        如果先下单后发消息，消息发失败 → 库存没扣（不一致）
        如果先发消息后下单，订单失败 → 库存扣了（不一致）

    → 需要"本地事务"和"消息发送"保持原子性
```

### 事务消息原理
```
    ① 生产者发送【半消息】（prepare，消费者不可见）
    ② 执行本地事务（如：写订单表）
    ③ 返回事务状态：
        · COMMIT → 半消息变正式消息，消费者可见
        · ROLLBACK → 删除半消息
        · UNKNOWN → Broker 会回查生产者询问结果（最多 15 次）
    ④ 生产者提供回查接口，根据本地事务状态返回结果
```

```
    发送半消息
        ↓
    执行本地事务
        ↓
    返回 COMMIT/ROLLBACK
        ├── COMMIT → 消息可见，消费者消费
        └── ROLLBACK → 丢弃消息
    （超时/未知 → Broker 回查生产者 → 再决定）
```

### 代码示例
```java
// ① 实现事务监听器
public class OrderTransactionListener implements TransactionListener {
    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        // 执行本地事务：写订单表
        int result = orderDao.insert(order);
        return result > 0 ? LocalTransactionState.COMMIT_MESSAGE   // 成功
                          : LocalTransactionState.ROLLBACK_MESSAGE; // 失败
    }

    @Override
    public LocalTransactionState checkLocalTransaction(Message msg) {
        // 回查：Broker 不确定时询问
        return orderDao.exists(orderId) ? LocalTransactionState.COMMIT_MESSAGE
                                        : LocalTransactionState.ROLLBACK_MESSAGE;
    }
}

// ② 发送半消息
TransactionMQProducer producer = new TransactionMQProducer("group");
producer.setTransactionListener(new OrderTransactionListener());
producer.sendMessageInTransaction(msg, order);
```

### 对比：本地消息表方案
```
    本地消息表（不用事务消息）：
        ① 业务表和消息表在【同一个本地事务】里写
        ② 定时任务扫描消息表，把"待发送"的消息发给 MQ
        ③ 收到 ack 后标记已发送

    优点：不依赖 MQ 特性，通用
    缺点：需要额外建表 + 定时任务

    事务消息：更优雅，但依赖 RocketMQ
```

---

## 九、死信队列与延时消息

### 0. ⭐⭐ 项目全链路速查：4 个队列 → 谁消费（2026-09-28 整理）

> **用途**：串"全链路"用。看到队列名 → 立刻知道谁在收、干什么。
> **对照方法**：全局搜 `@RabbitListener`，只有 2 处 → 对应 2 个消费者。

| 队列名 | 谁消费 | 代码位置 | 干什么 |
|---|---|---|---|
| `seckill.order.queue` | **`OrderConsumer.consume()`** | `@RabbitListener(queues = SECKILL_ORDER_QUEUE)` | insert 订单（status=0） |
| `seckill.order.delay.queue` | **❌ 没人（故意的）** | 无 `@RabbitListener` | 躺 30 分钟等 TTL 过期 |
| `seckill.order.timeout.queue` | **`OrderTimeoutConsumer.consume()`** | `@RabbitListener(queues = SECKILL_ORDER_TIMEOUT_QUEUE)` | 查状态 → 取消/跳过 |
| `seckill.order.dlq` | **❌ 没人（收尸用）** | 无 `@RabbitListener` | 存消费失败的消息 |

**交换机（2 个）**：
| 交换机 | 绑定 | 作用 |
|---|---|---|
| `seckill.delay.exchange` | → `delay.queue`（`delay.routingkey`） | 延迟消息入口 |
| `seckill.dlx` ⭐ | → `dlq`（`dlq.routingkey`）<br>→ `timeout.queue`（`timeout.routingkey`） | **死信汇聚点，两链路共用** |

**⭐ 全链路 3 跳**（串起来记这个）：

```
跳 1【发】SeckillService 发两条消息
  ├─ 第1条 → seckill.order.queue        （要立刻响应）
  └─ 第2条 → seckill.delay.exchange     （要定时）

跳 2【收】两个 @RabbitListener
  ├─ OrderConsumer        ← order.queue     → insert 订单
  └─ OrderTimeoutConsumer ← timeout.queue   → 取消/跳过
      （delay.queue 故意无消费者，专门"躺"）

跳 3【连】delay.queue --TTL过期--> RabbitMQ内核搬运 --> seckill.dlx --分拣--> timeout.queue
```

**⭐ MQ 术语 → 代码翻译表**（串不起来时用）：

| MQ 术语 | 在 Spring 代码里长什么样 |
|---|---|
| **生产者** | `rabbitTemplate.convertAndSend(...)` |
| **消费者** | **`@RabbitListener` 注解的方法** |
| **消费** | 那个方法**被执行** |
| **队列** | `new Queue("xxx")` |
| **绑定** | `BindingBuilder.bind(队列).to(交换机).with(rk)` |
| **路由** | 交换机按 `routingKey` 查绑定表 |

### 1. 死信队列（DLQ）
```
    定义：消费失败且重试多次仍失败的消息 → 进入死信队列

    触发条件（RocketMQ）：
        · 消费重试次数达到上限（默认 16 次）
        · 消息积压超过时间

    用途：
        · 隔离"坏消息"，不阻塞正常消费
        · 人工排查 / 定时任务补偿处理

    处理方式：
        · 监控死信队列，告警
        · 定时任务扫描死信，人工/自动修正后重新投递
```

### 2. 延时消息
```
    RocketMQ 原生支持：
        · 预设 18 个延时级别（1s / 5s / 10s / 30s / 1m ... 2h）
        · 生产时设置：msg.setDelayTimeLevel(3);  // 10 秒

    用途：
        · 订单超时未支付自动关闭（下单后延时 30 分钟检查）
        · 定时提醒、限流降级

    注意：
        · RocketMQ 只支持预设级别，不能自定义任意秒数
        · 自定义延时：用 ZSet 实现（score 存到期时间戳）
```

### 3. 用 Redis 实现延时队列（面试扩展）
```
    原理：ZSet + 定时轮询
        ZADD delay_queue 到期时间戳 消息内容
        定时任务 ZRANGEBYSCORE delay_queue -inf 当前时间（取出到期的）
        ZREM 删除已取出的（防止重复）

    局限：Redis 实现的延时队列没有 ack、重试、死信等能力
         复杂场景还是用 RocketMQ
```

### 4. ⭐ 实战：RabbitMQ 死信队列（我项目里亲手做的）

> 来源：秒杀项目 `RabbitMQConfig.java` + `application.yml`（2026-09-22 落地）
> 这是笔记里**唯一一段我自己写过的 MQ 代码**，面试直接讲这段。

#### 4.1 我的可靠性方案（三层）

```mermaid
graph LR
    A[生产者投递] --> B[业务队列<br/>seckill.order.queue<br/>持久化]
    B --> C{消费者处理}
    C -->|成功| D[ACK 确认]
    C -->|失败| E[重试 3 次<br/>1s/2s/4s]
    E -->|仍失败| F[DLX 死信交换机<br/>seckill.dlx]
    F --> G[DLQ 死信队列<br/>seckill.order.dlq]
    G --> H[人工/定时补偿]
```

| 层 | 做什么 | 我的配置 |
|---|---|---|
| **① 持久化** | broker 重启不丢 | 队列 `durable=true` |
| **② 消费者重试** | 临时故障自愈 | `max-attempts=3`、指数退避 1s/2s/4s |
| **③ 死信兜底** | 重试耗尽不丢消息 | `x-dead-letter-exchange` → DLX → DLQ |

#### 4.2 配置代码

**`application.yml`（消费者重试）**：
```yaml
spring:
  rabbitmq:
    listener:
      simple:
        prefetch: 1              # 每次拉 1 条（公平分发）
        acknowledge-mode: auto   # Spring AOP：正常返回→ack，抛异常→nack 重入队
        retry:
          enabled: true          # ⚠️ 默认 false，必须显式开启！
          max-attempts: 3        # 总共尝试 3 次（第1次 + 2次重试）
          initial-interval: 1000 # 首次间隔 1 秒
          multiplier: 2          # 间隔翻倍：1s → 2s → 4s
          max-interval: 10000    # 间隔上限 10 秒
```

**`RabbitMQConfig.java`（死信交换机 + 队列 + 绑定）**：
```java
// 1. 死信交换机（DLX）—— 按 routingKey 精确路由
@Bean
public DirectExchange seckillDlx() {
    return new DirectExchange(SECKILL_DLX, true, false);
}

// 2. 死信队列（DLQ）—— 收尸的地方
@Bean
public Queue seckillDlq() {
    return new Queue(SECKILL_DLQ, true);
}

// 3. 绑定：交换机 + routingKey → 队列
@Bean
public Binding dlqBinding() {
    return BindingBuilder.bind(seckillDlq())
            .to(seckillDlx())
            .with(SECKILL_DLQ_ROUTING_KEY);
}

// 4. ⭐ 业务队列：通过 arguments 指定「死信去哪」
@Bean
public Queue seckillOrderQueue() {
    Map<String, Object> args = new HashMap<>();
    args.put("x-dead-letter-exchange", SECKILL_DLX);              // 死信去哪个交换机
    args.put("x-dead-letter-routing-key", SECKILL_DLQ_ROUTING_KEY); // 用什么 routingKey
    return new Queue(SECKILL_ORDER_QUEUE, true, false, false, args);
}
```

#### 4.4 死信的三个触发条件（RabbitMQ）

```
    ① 消费者 nack/reject 且 requeue=false  ← 我项目用这个
    ② 消息 TTL 过期
    ③ 队列达到最大长度（x-max-length）
```

#### 4.5 ✅ 验证结果（2026-09-22 实测通过）

**验证方法**：加开关 `seckill.mq.fail-test=true`，让消费者在落库前抛异常。

**实测步骤与结果**：

| 步骤 | 命令/操作 | 结果 |
|---|---|---|
| 1. 触发下单 | `POST /api/seckill/redis?userId=999&goodsId=1` | `code:200`，返回订单号 `f104c869...` |
| 2. 观察重试 | 看应用日志 | `Retries exhausted for message` |
| 3. 观察拒绝 | 看应用日志 | `AmqpRejectAndDontRequeueException` |
| 4. 查队列 | `rabbitmqctl list_queues` | `seckill.order.queue=0`，**`seckill.order.dlq=1`** ✅ |

**关键日志（面试可背）**：
```
WARN o.s.a.r.r.RejectAndDontRequeueRecoverer :
    Retries exhausted for message (Body:'999:1:f104c8698ea446ddbf765d18cd0e16bf')

WARN s.a.r.l.ConditionalRejectingErrorHandler : Execution of Rabbit message listener failed.
ListenerExecutionFailedException: Retry Policy Exhausted
Caused by: org.springframework.amqp.AmqpRejectAndDontRequeueException
```

**链路闭环**（图解）：
```mermaid
graph LR
    A[POST /api/seckill/redis] --> B[投递消息<br/>999:1:f104c...]
    B --> C[消费者抛异常]
    C --> D[重试 3 次<br/>1s → 2s → 4s]
    D --> E[Retries exhausted]
    E --> F[AmqpRejectAndDontRequeueException]
    F --> G[死信队列<br/>seckill.order.dlq = 1条 ✅]
```

#### 4.6 ⚠️ 踩坑记录（今天真踩的）

**坑 1：队列参数不能修改**
- RabbitMQ 里已存在的队列，**参数是固定的**，改了代码会报：
  `PRECONDITION_FAILED - inequivalent arg 'x-dead-letter-exchange'`
- **解决**：先删旧队列（`rabbitmqctl delete_queue xxx`），重启让应用重建

**坑 2：`listener.simple.retry.enabled` 默认是 `false`**
- 只写 `max-attempts` 不写 `enabled: true` → **配置完全不生效**
- 症状：消费失败后**无限重入队**（死循环刷日志）

**坑 3：别把 `template.retry` 当消费者重试**
- `spring.rabbitmq.template.retry.*` = **生产者发送失败**的重试
- `spring.rabbitmq.listener.simple.retry.*` = **消费者处理失败**的重试
- **两者完全不同，别配错地方**

**坑 4：`@Value` 导错包（今天真踩）**
- 想注入配置，导成了 `import lombok.Value;`
- **Spring 的是** `org.springframework.beans.factory.annotation.Value`
- 导错包**编译能过**，但注入**完全失效**（值永远是默认值），排查很痛苦
- **口诀**：`Value` 在 **spring.beans.factory.annotation** 包下

**坑 5：测试异常抛在落库之后（今天真踩）**
- 把 `throw` 写在 `orderMapper.insert(order)` **之后** → 消息**已落库**才失败
- 后果：重试 3 次就是**重复插入 3 次** → 脏数据（除非有唯一键挡住）
- **正确**：失败模拟必须放在**副作用之前**（落库/扣库存/发短信前）

#### 4.7 面试话术（可直接背）

> **问：你怎么保证消息不丢？**
>
> 我从三个环节保证：
> **① 生产者**：队列**持久化**（durable），broker 重启消息还在；
> **② 消费者**：用 Spring 的 `auto` ack + **重试 3 次**（1s/2s/4s 指数退避），临时故障能自愈；
> **③ 兜底**：重试耗尽后消息**不会丢**——我在业务队列上绑了**死信交换机（DLX）**，失败消息自动转到**死信队列（DLQ）**，可以人工或定时补偿。
>
> **追问：ack 模式用的什么？为什么不手动 ack？**
> 用 Spring 的 `auto`。它比纯自动 ack 强——方法**抛异常会 nack 重入队**，配合重试上限和死信，可靠性已经闭环。手动 ack 控制更细，但**写错容易丢消息**（比如异常分支忘了 nack），所以我先用 auto + 重试 + 死信这套，够用且安全。
>
> **追问：自动 ack 不是会丢消息吗？**
> 你说的"自动 ack"是 `none` 模式（投递即确认）；**Spring 的 `auto` 不同**，它是 AOP 拦截：正常返回才 ack，抛异常会 nack。**名字像，机制完全不同**，这是面试常见陷阱。

#### 4.8 现状与待改进（诚实标注）

- ✅ 已做：持久化队列、消费者重试、死信队列（DLX + DLQ）、**实测验证通过**（2026-09-22）
- ✅ 已做：**生产者 confirm 发布确认**（2026-09-27 完成并验证）
- ❌ 未做：**手动 ACK**（更精细的控制）
- ❌ 未做：**死信队列的消费/告警**（消息进了 DLQ 没人知道）
- 📌 下一步：加死信消费监控

---

#### 4.8b ⭐⭐ 生产者 confirm（2026-09-27 完成，我自己踩过坑）

##### 一、为什么需要它（`convertAndSend` 的"盲区"）

**问题**：`convertAndSend` **成功返回 ≠ broker 真的收到了！**

```java
rabbitTemplate.convertAndSend(队列, 消息);
return Result.success("抢购成功");   // ← 立刻返回"成功"
```

**可能失败的情况**：网络断了 / broker 挂了 / 消息被拒 → **消息根本没到 broker**，
但**你的代码还是打印"成功"** → 用户以为下单了，**实际消息丢了，订单永远不落库**。

**confirm 就是解决这个**：**broker 收到消息后，主动回调通知生产者。**

##### 二、用"寄快递"理解

| MQ 概念 | 快递类比 |
|---|---|
| 生产者发送 | 你寄包裹 |
| **`CorrelationData`** | **快递单号**（你贴的，用于辨认哪一单） |
| **`ConfirmCallback.confirm()`** | **快递员打电话回执**（broker 主动通知你） |
| **`ack`** | 快递员的回答：**"收到了" / "没收到"** |

**没有 confirm** = 你把包裹丢进柜子，**没有任何回执，心里没底**。
**有 confirm** = 快递柜**主动打电话**告诉你"单号 XXX 已收到"。

##### 三、完整流程（异步回调！）

```mermaid
sequenceDiagram
    participant S as 生产者 SeckillService
    participant C as MqConfirmCallback
    participant B as RabbitMQ Broker
    participant Q as 队列

    S->>S: 1. new CorrelationData("orderNo:order")
    S->>B: 2. convertAndSend(交换机, key, 消息, CorrelationData)
    Note over S: 3. 【立即返回】，不等结果
    B->>Q: 4. 消息落盘进队列
    B-->>C: 5. 【异步回调】confirm(ack=true, correlationData)
    C->>C: 6. 打印"broker 已收到，id=xxx"
```

**⚠️ 最容易误解的点**：
> **步骤 3 和 5 是分开的** —— `convertAndSend` **发完就返回**，
> **confirm 回调是"稍后异步"执行的**（不是同步等待）。

##### 四、代码实现（3 部分）

**① `application.yml` 开关**：
```yaml
spring:
  rabbitmq:
    publisher-confirm-type: correlated   # 开启 confirm（异步回调 + 关联ID）
    publisher-returns: true              # 顺便开启 return（路由失败通知）
```
- `publisher-confirm-type` 三个值：`none`（默认，关闭）/ `simple`（同步等待）/ **`correlated`（推荐，异步回调）**

**② 回调类**：
```java
@Component
public class MqConfirmCallback implements RabbitTemplate.ConfirmCallback {

    public MqConfirmCallback(RabbitTemplate rabbitTemplate) {
        rabbitTemplate.setConfirmCallback(this);   // ⚠️ 全局设置，会覆盖之前的
    }

    @Override
    public void confirm(@Nullable CorrelationData correlationData, boolean ack, @Nullable String cause) {
        // ⚠️ correlationData 可能为 null（框架内部消息不带），必须判空！
        String id = correlationData != null ? correlationData.getId() : "无ID";
        if (ack) {
            System.out.println("【confirm】broker 已收到，id=" + id);
        } else {
            System.out.println("【confirm】❌ broker 未收到！id=" + id + "，原因=" + cause);
        }
    }
}
```

**③ 发送时带 CorrelationData**：
```java
rabbitTemplate.convertAndSend(
        "",                                     // 默认交换机
        RabbitMQConfig.SECKILL_ORDER_QUEUE,     // routingKey = 队列名
        userId + ":" + goodsId + ":" + orderNo,
        new CorrelationData(orderNo + ":order"));
```

##### 五、⚠️ 踩坑记录（我真实遇到的）

**坑 1：`convertAndSend(队列名, 消息)` 是"语法糖"**

```java
// 你一直这么写，以为是"直接发队列"
convertAndSend("seckill.order.queue", "消息");

// 实际等价于：
convertAndSend("", "seckill.order.queue", "消息", null);
//             ↑        ↑
//          默认交换机   routingKey（恰好等于队列名）
```
> **记住：消息永远先经过交换机**，即使你看不到它（默认交换机 `""`）。

**坑 2：「重载歧义」编译错误**（真踩了）

```java
convertAndSend("队列名", "消息", new CorrelationData("id"));   // ❌ 编译错误
// 报错：对 convertAndSend 的引用不明确
//   (String, Object, CorrelationData) 和 (String, String, Object) 都匹配
```

**RabbitTemplate 的相关重载**：
| 重载 | 状态 |
|---|---|
| `(String exchange, String routingKey, Object message)` | ✅ 存在 |
| `(String exchange, Object message, CorrelationData data)` | ✅ 存在（⚠️ **注意顺序**） |
| `(String exchange, String routingKey, Object message, CorrelationData data)` | ✅ 存在 |
| `(String queueName, Object message, CorrelationData)` | ❌ **不存在** |

**解法**：**加 `CorrelationData` 必须用【4 参版】**（**显式写出交换机和 routingKey**）。

**坑 3：`correlationData` 可能为 null**
- 框架内部发的消息**不带** `CorrelationData` → 直接 `.getId()` 会 **NPE**
- **必须判空**

##### 六、⚠️ confirm 的"兄弟"：ReturnsCallback（易混）

| | **ConfirmCallback** | **ReturnsCallback** |
|---|---|---|
| **确认什么** | **消息到 broker 了吗** | **消息路由到队列了吗** |
| **哪一层** | **生产者 → Broker** | **Broker → 队列** |
| **触发** | broker 收到消息 | **路由失败**（找不到队列） |

**例子**：
```
发消息给交换机，routingKey="abc"，但没有队列绑定 "abc"
  ↓
① ConfirmCallback → ack=true（"broker 收到了" ✅）
② ReturnsCallback → 触发（"路由不到队列" ❌）
```

> **⭐ 关键认知**：**`confirm=true` 不代表"消息进了队列"！只代表"broker 收到了"。**
> 要确认"路由成功"，**还需要 `ReturnsCallback`**。（这个细节很多人不知道，说出来加分）

##### 七、验证结果（2026-09-27 实测）

**一条下单 = 发两条消息 → 两次 confirm 回调**：
```
【confirm】broker 已收到，id=6ca948b4c4914215b414377e2255c3f6:order   ← 异步落单
【confirm】broker 已收到，id=6ca948b4c4914215b414377e2255c3f6:delay   ← 延迟消息
```
> **ID 加后缀 `:order` / `:delay`** 是为了**区分同一次下单的两条消息**（否则都是同一个 orderNo，分不清）。

##### 八、⭐ MQ 可靠性完整三层（现在齐了）

```mermaid
graph LR
    A["① 生产者"] -->|"confirm 发布确认"| B["② Broker"]
    B -->|"durable 持久化"| C["③ 消费者"]
    C -->|"重试 3 次 + 死信队列"| D["兜底"]
```

| 层 | 机制 | 防止什么 |
|---|---|---|
| **① 生产者 → Broker** | **confirm 发布确认** | 消息没到 broker |
| **② Broker 存储** | 队列/消息**持久化**（durable） | broker 宕机丢失 |
| **③ Broker → 消费者** | **重试 3 次 + 死信队列** | 消费者处理失败丢失 |

**面试话术（完整版）**：
> "消息不丢我做了三层：
> **① 生产者层**用 **confirm 发布确认**——消息发出后 broker 会异步回调通知是否收到，成功 `ack=true`，失败能感知；
> **② Broker 层**用**持久化**（队列 durable），broker 重启消息还在；
> **③ 消费者层**用 **auto ack + 重试 3 次**，重试耗尽后经**死信交换机转进死信队列**，不丢可补偿。
> 我还实测验证过每一层。"

---

> **【学习提示】** 本节知识是我在 MQ 前置知识不足时"边做边学"的产物。
> **待补的前置知识**（看教程时带这些问题）：
> 1. RabbitMQ 的 Exchange / Queue / Binding 到底是什么关系？
> 2. `x-dead-letter-*` 这类队列参数还有哪些？分别什么作用？
> 3. `acknowledge-mode` 的 none / auto / manual 到底差在哪？
> 4. Spring AMQP 的 retry 拦截器是怎么织入监听方法的？

#### 4.9 ⭐ 秒杀项目 MQ 完整拓扑图（2026-09-23 定稿）

**代码位置**：
- `RabbitMQConfig.java` —— 订单削峰 + 消费失败兜底
- `OrderTimeoutMQConfig.java` —— 订单超时自动取消（延迟队列）

```mermaid
graph TB
    subgraph L1["链路一：正常下单 + 消费失败兜底"]
        P1["生产者<br/>下单成功"] -->|"routingKey = 队列名"| BQ["seckill.order.queue<br/>业务队列（持久化）"]
        BQ -->|消费成功| OK["✅ 订单落库"]
        BQ -->|"消费失败，重试3次仍失败"| DLX
    end

    subgraph L2["链路二：订单超时自动取消（延迟队列）"]
        P2["生产者<br/>下单成功"] -->|"delay.routingkey"| DE["seckill.delay.exchange"]
        DE -->|"delay.routingkey"| DLYQ["seckill.order.delay.queue<br/>⏰ TTL = 30min<br/>【无消费者】"]
        DLYQ -->|"消息过期，变死信"| DLX
    end

    DLX["🔀 seckill.dlx<br/>死信交换机<br/>【两条链路复用】"]
    DLX -->|"dlq.routingkey"| DLQ["seckill.order.dlq<br/>失败死信队列<br/>【人工/定时补偿】"]
    DLX -->|"timeout.routingkey"| TQ["seckill.order.timeout.queue<br/>超时处理队列"]
    TQ --> C["消费者<br/>查订单状态"]
    C -->|"待支付 status=0"| CANCEL["取消订单 + 回补库存"]
    C -->|"已支付 status=1"| SKIP["什么都不做"]
```

**关键设计点**（面试可讲）：

| 点 | 说明 |
|---|---|
| **交换机复用** | `seckill.dlx` 被**两条链路共用** —— 靠**不同 routingKey** 分发到不同队列，这就是"交换机一对多" |
| **队列不复用** | `seckill.order.dlq`（失败）≠ `seckill.order.timeout.queue`（超时），**用途不同必须分开** |
| **延迟队列无消费者** | 延迟队列故意**不配消费者**，让消息躺着等 TTL 过期 |
| **TTL + DLX = 延迟队列** | RabbitMQ 没有原生延迟消息，用"TTL 过期 + 死信路由"模拟 |
| **两种死信产生方式** | ① 消费失败被 reject ② 消息 TTL 过期 —— **同一个 DLX 机制** |

**复用判断标准（今天想通的）**：
> **交换机 = 分拣中心 → 天生公用，可复用**
> **队列 = 终点 → 一个用途一个队列，不可复用**

---

#### 4.10 ⭐⭐ 消息流动全追踪（一次下单 = 两条消息）

> **这是理解整个项目的钥匙**。先看"怎么写"，再看"怎么走"。

##### 一、发送的 2 种写法（混乱的根源）

```java
// 写法1：发到【队列名】—— 走【默认交换机】，routingKey 就是队列名
rabbitTemplate.convertAndSend("seckill.order.queue", msg);

// 写法2：发到【交换机 + routingKey】—— 显式指定
rabbitTemplate.convertAndSend("seckill.delay.exchange", "delay.routingkey", msg);
```

| 写法 | 谁在用 | 实际路径 |
|---|---|---|
| **写法1** | 下单消息（异步落单） | 默认交换机 `""` → 按 routingKey（=队列名）投递 |
| **写法2** | 延迟消息（超时检查） | `seckill.delay.exchange` → 按 routingKey 路由 |

**❗核心认知**：**任何消息都先经过交换机**，即使写法1也有"默认交换机"。

##### 二、一次下单 = 发出【两条】消息

```java
// SeckillService.doSeckillByRedis()
String orderNo = UUID.randomUUID().toString().replace("-", "");

// ★ 消息A：异步落单（立刻处理）
rabbitTemplate.convertAndSend(RabbitMQConfig.SECKILL_ORDER_QUEUE,
        userId + ":" + goodsId + ":" + orderNo);

// ★ 消息B：延迟消息（预约 30 分钟后检查）
rabbitTemplate.convertAndSend(
        OrderTimeoutMQConfig.SECKILL_DELAY_EXCHANGE,
        OrderTimeoutMQConfig.SECKILL_ORDER_DELAY_ROUTING_KEY,
        userId + ":" + goodsId + ":" + orderNo);

return Result.success("抢购成功，订单处理中，订单号：" + orderNo);  // ← 立刻返回
```

**两条消息内容一样，但路线/目的完全不同**：

| | 消息A | 消息B |
|---|---|---|
| **目的地** | `seckill.order.queue` | 延迟队列 → 处理队列 |
| **耗时** | 几十毫秒 | 30 分钟 |
| **目的** | **立刻落单** | **30分钟后检查是否该取消** |
| **消费者** | `OrderConsumer` | `OrderTimeoutConsumer` |

##### 三、消息 A 的旅程（几十毫秒，异步落单）

```
① convertAndSend("seckill.order.queue", "1:2:abc123")
② → 默认交换机（routingKey = "seckill.order.queue"）
③ → 路由到 seckill.order.queue
④ → OrderConsumer 消费：
      split(":") → userId=1, goodsId=2, orderNo=abc123
      → orderMapper.insert(order)
⑤ 订单落库 ✅
```

##### 四、消息 B 的旅程（30 分钟，超时检查）

```
① convertAndSend("seckill.delay.exchange", "delay.routingkey", "1:2:abc123")
② → seckill.delay.exchange 交换机
③ → routingKey 匹配 → seckill.order.delay.queue（延迟队列）
④ ⏰ 消息【躺着】…… 躺满 30 分钟 ……
⑤ TTL 过期 → 消息变【死信】
⑥ → seckill.dlx（死信交换机）
⑦ → 按 x-dead-letter-routing-key = "timeout.routingkey"
⑧ → seckill.order.timeout.queue（处理队列）
⑨ → OrderTimeoutConsumer 消费：
      selectByOrderNo("abc123") 查状态
      ├─ status=0（待支付）→ cancel() + addStock()  ← 取消+还库存
      ├─ status=1（已支付）→ 跳过，什么都不做
      └─ status=2（已取消）→ 跳过（幂等）
```

##### 五、⭐ 延迟消息 vs 即时消息（最关键的区别）

| | 即时消息（消息A） | 延迟消息（消息B） |
|---|---|---|
| **发送时** | 状态**已知**（刚下单，必是待支付） | 状态**未知**（30分钟后才知道） |
| **处理时** | 直接处理 | **必须重新查状态** |

**❗为什么延迟消息的消费者一定要"先查订单状态"？**

```java
// ❌ 错误：消息一到就取消
orderMapper.cancel(orderNo);
goodsMapper.addStock(goodsId);
// → 会把【已付款】的订单也取消掉！灾难

// ✅ 正确：先查，只取消"待支付"的
if (order.getStatus() == 0) {
    orderMapper.cancel(orderNo);
    goodsMapper.addStock(goodsId);
}
```

**因为消息是 30 分钟前发出的** —— 这 30 分钟里用户**可能已经付款了**。

> **一句话**：**延迟消息 = 对"未来状态"的操作，处理时必须以"当前实际状态"为准。**

##### 六、消息流动的通用规律（4 步，永远如此）

```
生产者 → 交换机 → (按 routingKey) → 队列 → 消费者
  ①        ②           ③            ④
```

**记住**：**绕不开交换机**，哪怕你看不到它（默认交换机）。

##### 七、幂等设计（重复消息怎么办）

| 机制 | 位置 | 作用 |
|---|---|---|
| 消息A | `insert` + `order_no` 唯一键 | 重复投递 → `DuplicateKeyException` → 落单只成功一次 |
| 消息B | `cancel` 带 `WHERE status=0` | 重复投递 → 影响行数 0 → **不回补库存**（不重复加） |

**关键**：**"条件更新 + 检查影响行数"** 是 MQ 消费端幂等的标准做法。

---

## 十、高可用架构

### RocketMQ 高可用
```
    架构：
        · 多个 NameServer（互相独立，不通信）
        · Broker 主从架构（Master 写，Slave 备份）
        · 主从同步：同步复制 / 异步复制

    故障处理：
        · 主 Broker 挂了 → 从 Broker 顶上（读取不受影响）
        · 写操作需等主恢复或新主
```

### Kafka 高可用
```
    架构：
        · Topic 分多个 Partition
        · 每个 Partition 有多个副本（Leader + Follower）
        · Leader 负责读写，Follower 同步数据

    故障处理：
        · Leader 挂了 → 从 ISR（同步副本）中选举新 Leader
        · 副本在多个 Broker 上分布（防止单机故障）

    ISR 机制：
        · ISR = 和 Leader 保持同步的副本集合
        · acks=all 时，ISR 全部确认才算成功
        · Follower 落后太多被踢出 ISR
```

### 对比总结
| 维度 | RocketMQ | Kafka |
|------|---------|-------|
| 高可用 | NameServer + 主从 | 分区副本 + ISR 选举 |
| 故障切换 | 从节点读，主恢复写 | Leader 选举 |
| 数据安全 | 同步刷盘 + 主从同步 | acks=all + ISR |

---

## 十一、面试考点汇总

### ⭐⭐⭐⭐⭐ 必考
1. **为什么用 MQ？**（异步解耦、削峰填谷、数据分发）
2. **如何保证消息不丢失？**（生产/存储/消费三环节）
3. **如何保证消息不重复消费？**（幂等：唯一 ID + 唯一约束）
4. **消息积压怎么处理？**（扩容 + 转存 + 优化消费）
5. **如何保证消息顺序？**（同 key 同队列 + 单线程消费）

### ⭐⭐⭐⭐ 高频
6. **RocketMQ 和 Kafka 的区别？怎么选？**
7. **事务消息的原理？**
8. **死信队列是什么？有什么用？** ← 我项目做了（见 九.4）
9. **延时消息怎么实现？**
10. **集群消费和广播消费的区别？**
11. **ack 模式有哪几种？auto 和 none 的区别？** ← 陷阱题（见 九.4.5）

### ⭐⭐⭐ 中频
11. **同步刷盘和异步刷盘的区别？**
12. **Kafka 的 ISR 机制是什么？**
13. **消费组的概念？一个分区能被多个消费者消费吗？**
14. **消息队列的高可用怎么实现？**
15. **本地消息表方案 vs 事务消息？**

### 💡 高频追问
```
    · 消费者挂了消息会丢吗？
    → 不会，Broker 持久化了，重启后从 offset 继续消费

    · 消息重复消费怎么办？
    → 幂等设计（唯一 ID + 数据库唯一约束）

    · 为什么 Kafka 吞吐量最高？
    → 顺序写磁盘（追加日志）、零拷贝、批量发送、分区并行

    · 线上消息积压了几百万条怎么办？
    → 先扩容消费者恢复消费 → 临时转存 → 优化消费逻辑 → 追数据

    · MQ 和 Redis 的 List 都能当队列，区别？
    → MQ 有可靠性（ack/重试/死信）、事务、积压管理；Redis 简单轻量
      复杂业务用 MQ，简单场景 Redis 够用
```

---

## 十二、自查清单

```
    [ ] 能说出 MQ 的三大核心作用（异步/削峰/分发）
    [ ] 能说出引入 MQ 的代价
    [ ] 能对比 RocketMQ / Kafka / RabbitMQ 并说出选型依据
    [ ] 能说出消息丢失的三个环节及各自兜底方案
    [ ] 能说出至少 3 种幂等方案
    [ ] 能说出消息积压的处理步骤（止血→转存→优化→追数）
    [ ] 能画出顺序消息的实现方案（同 key 同队列）
    [ ] 能画出事务消息的流程（半消息→本地事务→回查）
    [ ] 能说出死信队列的触发条件和用途
    [ ] 能说出延时消息的实现方式
    [ ] 能说出 RocketMQ 和 Kafka 的高可用架构
    [ ] 能说出集群消费和广播消费的区别
    [ ] 能说出 Kafka 吞吐量高的原因
    [ ] 能说出本地消息表 vs 事务消息的优劣
```
