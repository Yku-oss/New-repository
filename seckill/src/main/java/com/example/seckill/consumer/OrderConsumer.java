package com.example.seckill.consumer;

import com.example.seckill.config.RabbitMQConfig;
import com.example.seckill.entity.SeckillOrder;
import com.example.seckill.mapper.SeckillOrderMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 秒杀订单消费者 —— MQ 削峰的核心
 *
 * 主流程（doSeckillByRedis）扣完库存后只发一条消息就返回，
 * 真正"落单"由这里异步完成：MQ 把洪峰排队，消费者按自己节奏慢慢落库，DB 不被瞬时打爆。
 */
@Component
public class OrderConsumer {

    private final SeckillOrderMapper orderMapper;
    private final RabbitTemplate rabbitTemplate;

    @Value("${seckill.mq.fail-test:false}")
    private boolean failTest;


    public OrderConsumer(SeckillOrderMapper orderMapper, RabbitTemplate rabbitTemplate) {
        this.orderMapper = orderMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.SECKILL_ORDER_QUEUE)
    public void consume(String message) {
        // 消息格式：userId:goodsId:orderNo
        String[] parts = message.split(":");
        Long userId = Long.valueOf(parts[0]);
        Long goodsId = Long.valueOf(parts[1]);
        String orderNo = parts[2];

        // 测试开关：故意失败，用于验证「重试 → 死信队列」链路
        // 必须放在落库【之前】，否则数据会脏（重试会重复插入）
        if (failTest) {
            throw new RuntimeException("【测试】故意失败，验证死信链路 " + orderNo);
        }

        // 重建订单并落库（异步）
        SeckillOrder order = new SeckillOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setGoodsId(goodsId);
        order.setStatus(0);
        orderMapper.insert(order);

        System.out.println("【MQ异步落单】orderNo=" + orderNo + ", userId=" + userId + ", goodsId=" + goodsId);
    }

}
