package com.example.seckill.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.seckill.config.OrderTimeoutMQConfig;
import com.example.seckill.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.seckill.common.Result;
import com.example.seckill.entity.SeckillGoods;
import com.example.seckill.entity.SeckillOrder;
import com.example.seckill.mapper.SeckillGoodsMapper;
import com.example.seckill.mapper.SeckillOrderMapper;


/**
 * 秒杀服务 — 模块1（DB 层兜底版）
 *
 * 核心：超卖问题（Oversell）
 *   bug 版（check-then-act 先查后改）：SELECT stock → 判断 >0 → UPDATE
 *   两个并发请求都读到 stock=1、都通过判断、都 UPDATE → 库存变 -1（超卖）
 *
 *   本版（正确版）：UPDATE ... SET stock = stock - 1 WHERE id = ? AND stock > 0
 *   MySQL 行锁保证这条语句原子，天然防超卖 —— 这是最后一道防线
 *   （前面的防线：Redis 预减库存 + Lua，模块2 再做）
 */
@Service
public class SeckillService {
    //声明类，声明引用的类与接口
    private final RabbitTemplate rabbitTemplate;
    private final SeckillGoodsMapper goodsMapper;
    private final SeckillOrderMapper orderMapper;
    private final StringRedisTemplate stringRedisTemplate;
    //构造器，也就是将所引入的类或接口进行实例化
        public SeckillService(SeckillGoodsMapper goodsMapper, SeckillOrderMapper orderMapper,
                      StringRedisTemplate stringRedisTemplate, RabbitTemplate rabbitTemplate) {
    this.goodsMapper = goodsMapper;
    this.orderMapper = orderMapper;
    this.stringRedisTemplate = stringRedisTemplate;
    this.rabbitTemplate = rabbitTemplate;// ← 新加这行
}
    @Transactional
    public Result<?> doSeckill(Long userId, Long goodsId) {
        // 1. 查商品 + 校验秒杀时间窗
        SeckillGoods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            // 先查看库存是否存在，不存在就返回404，404在网络中是搜索不到，不存在。
            return Result.error(404, "商品不存在");
        }
        // LocalDateTime 是时间方法类，是java自带的方法包，记录当前时间
        LocalDateTime now = LocalDateTime.now();
        //查看当前下单的时候是否在秒杀时间内，如果不在就返回400，400是请求出错
        if (now.isBefore(goods.getStartTime()) || now.isAfter(goods.getEndTime())) {
            return Result.error(400, "不在秒杀时间窗口内");
        }

        // 2. 一人一单（业务层预检；DB 唯一键 uk_user_goods 是并发下的最终兜底）
        if (orderMapper.selectByUserAndGoods(userId, goodsId) != null) {
            return Result.error(400, "每人限购一件");
        }

        // 3. DB 条件扣减：库存足够才扣，行锁保证原子（防超卖核心）
        int rows = goodsMapper.deductStock(goodsId);
        if (rows == 0) {
            return Result.error(400, "库存不足");
        }

        // 4. 落订单（order_no 唯一键兜底幂等；并发重复下单会抛 DuplicateKeyException
        //    → 全局异常处理器兜底 → 事务回滚，包括第3步的扣库存）
        // 实现了MQ异步削峰，异步落单，先预减扣除DB，再进行落单
        // 但是MQ不能直接发送java对象，是生成了以一个UUID转变成String形式后，再发送给后面新new的订单对象中
        String orderNo = UUID.randomUUID().toString().replace("-", "");
         rabbitTemplate.convertAndSend(RabbitMQConfig.SECKILL_ORDER_QUEUE,
                  userId + ":" + goodsId + ":" + orderNo);
        // 落单交给 MQ 消费者异步完成，这里不再同步 insert（否则会重复落单、订单号还不一致）

        return Result.success("抢购成功，订单处理中，订单号：" + orderNo);

    }

        // ① Lua 脚本（常量，放字段下面）
    //作用是先查看当前商品的库存，然后通过判断库存的多少来返回订单状态，比如库存不足 0，已支付 1，商品不存在-1
    // 这段原子代码的主要作用是档流量的
private static final String STOCK_LUA =
    "local stock = tonumber(redis.call('GET', KEYS[1])) " +
    "if stock == nil then return -1 end " +
    "if stock < tonumber(ARGV[1]) then return 0 end " +
    "redis.call('DECRBY', KEYS[1], ARGV[1]) " +
    "return 1";

    // ③ 令牌桶限流 Lua 脚本
    // 令牌桶是让Redis给数据库DB进行兜底的，防止DB被请求流量打爆
    // 原理是线程得先取令牌，没有令牌的线程停止进行，直到令牌桶又补充有，令牌桶如果不是满的话，是按照规定时间进行补充令牌的
    // 这一段的主要作用就是挡住大量请求的，每一个用户进行请求时要有时间间隔限制
    // 先去保存用户进行第一个请求的时间，如果下一个请求没有到规定时间，那么就不会进行
    private static final String TOKEN_BUCKET_LUA =
            "local key = KEYS[1] " +
                    "local capacity = tonumber(ARGV[1]) " +
                    "local rate = tonumber(ARGV[2]) " +
                    "local time = redis.call('TIME') " +       // Redis 取当前时间(秒+微秒)
                    "local now = tonumber(time[1]) " +          // 取秒数
                    "local bucket = redis.call('HMGET', key, 'tokens', 'last_refill') " +
                    "local tokens = tonumber(bucket[1]) " +
                    "local last = tonumber(bucket[2]) " +
                    "if tokens == nil then tokens = capacity end " +
                    "if last == nil then last = now end " +
                    "local elapsed = now - last " +
                    "tokens = math.min(capacity, tokens + (elapsed * rate)) " + // 补充令牌
                    "if tokens >= 1 then " +
                    "  tokens = tokens - 1 " +
                    "  redis.call('HSET', key, 'tokens', tokens, 'last_refill', now) " +
                    "  return 1 " +
                    "else " +
                    "  return 0 " +
                    "end";

 public Long tryTokenBucket(String key, long capacity, long rate) {
        return stringRedisTemplate.execute(
                new DefaultRedisScript<>(TOKEN_BUCKET_LUA, Long.class),
                List.of(key),
                String.valueOf(capacity), String.valueOf(rate)
        );
    }


    // ② 方法（放在 testIncrement 下面）
@Transactional
public Result<?> doSeckillByRedis(Long userId, Long goodsId) {
    // 全局限流窗口
    Long allowed = tryTokenBucket("seckill:limit:all", 10, 5);
    if(allowed == null || allowed == 0){
        return  Result.error(429, "请求过于频繁，请稍后重试");
    }
    
    
    // ===== 一人一单（预检，放在最前面省资源）=====
    // 这个用户已经抢过这个商品 → 直接拒绝，不用去扣 Redis 库存
    if (orderMapper.selectByUserAndGoods(userId, goodsId) != null) {
        return Result.error(400, "每人限购一件");
    }

    Long result = stringRedisTemplate.execute(
            new DefaultRedisScript<>(STOCK_LUA, Long.class),   // "让 Redis 执行这段 Lua，返回数字"
            List.of("seckill:stock:" + goodsId),               // "脚本里 KEYS[1] 用这个 key"
            "1");                                              // "脚本里 ARGV[1] 用 1"（直接传，别包 List）

    if (result == null) return Result.error(500, "系统繁忙");
    if (result == -1) return Result.error(400, "商品不存在");
    if (result == 0)  return Result.error(400, "已售罄");
    // DB兜底，从数据库中扣除
    int rows = goodsMapper.deductStock(goodsId);
    if (rows == 0) {
        return Result.error(400, "库存不足");
    }
    // 扣成功 → 发 MQ 消息（异步落单，削峰），不再同步 insert




    // 创建订单order，创建uuid并且字符串话，并且将uuid前面的 - 号去掉
    String orderNo = UUID.randomUUID().toString().replace("-", "");
    // 消息格式：userId:goodsId:orderNo（消费者靠这三样重建订单）
    //
    // 【数据流动】convertAndSend(交换机, routingKey, 消息, 关联数据)
    //   · 交换机 = ""（空字符串）= 默认交换机
    //   · routingKey = 队列名 → 默认交换机按这个名字把消息投进队列
    //   · CorrelationData = 给消息贴"身份证"，confirm 回调时靠它认出是哪条消息
    // 注意：加 CorrelationData 必须用 4 参版（显式写出交换机和 routingKey），
    //       不能用 convertAndSend(队列名, 消息) 这种 2 参简化写法（会重载歧义）
    rabbitTemplate.convertAndSend(
            "",                                        // 默认交换机
            RabbitMQConfig.SECKILL_ORDER_QUEUE,        // routingKey = 队列名
            userId + ":" + goodsId + ":" + orderNo,
            new CorrelationData(orderNo + ":order"));

    // 同时发一条【延迟消息】：30 分钟后若仍未支付，自动取消订单 + 回补库存
    // 注意：发到【延迟交换机】，不是队列！消息靠 routingKey 路由进延迟队列，
    //       在延迟队列里躺够 TTL(30分钟)，过期变死信，最终到达超时处理队列
    rabbitTemplate.convertAndSend(
            OrderTimeoutMQConfig.SECKILL_DELAY_EXCHANGE,
            OrderTimeoutMQConfig.SECKILL_ORDER_DELAY_ROUTING_KEY,
            userId + ":" + goodsId + ":" + orderNo,
            new CorrelationData(orderNo + ":delay"));

    return Result.success("抢购成功，订单处理中，订单号：" + orderNo);
}



    @Transactional
    public Result<?> payOrder(String orderNo) {
        int lows = orderMapper.makePaid(orderNo);
        if(lows == 0){
            return Result.error(400,"订单不存在");
        }
        return Result.success("下单成功");
    }

        @Transactional
    public Result<?> cancelOrder(String orderNo) {
        // 1. 查订单，拿到 goodsId（取消要还库存，得知道是哪个商品）
        SeckillOrder order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        // 2. 取消订单（只有 status=0 待支付的才能取消成 2）
        int rows = orderMapper.cancel(orderNo);
        if (rows == 0) {
            return Result.error(400, "订单不是待支付状态，无法取消");
        }
        // 3. 取消成功 → 把库存加回去（用订单里的 goodsId）
        goodsMapper.addStock(order.getGoodsId());
        return Result.success("取消成功");
    }
        // 消息队列
        public void sendOrderMassage(String orderNo) {
            rabbitTemplate.convertAndSend(RabbitMQConfig.SECKILL_ORDER_QUEUE, orderNo);
        }


}
   
