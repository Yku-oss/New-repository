package com.example.seckill.config;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class MqConfirmCallback implements RabbitTemplate.ConfirmCallback{

    // 构造器注入 RabbitTemplate，然后在里面 setter 设置回调
    public MqConfirmCallback(RabbitTemplate rabbitTemplate) {
        rabbitTemplate.setConfirmCallback(this);
        // ⚠️ 注意：RabbitTemplate 是单例，这样设置会"覆盖"其他设置
    }


    @Override
    public void confirm(@Nullable CorrelationData correlationData, boolean ack, @Nullable String cause) {
        // ⚠️ correlationData 可能为 null（框架内部消息不带），必须判空
        String id = correlationData != null ? correlationData.getId() : "无ID";

        if (ack) {
            System.out.println("【confirm】broker 已收到，id=" + id);
        } else {
            System.out.println("【confirm】❌ broker 未收到！id=" + id + "，原因=" + cause);
        }
    }
}
