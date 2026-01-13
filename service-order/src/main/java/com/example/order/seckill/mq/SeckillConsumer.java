package com.example.order.seckill.mq;

import com.example.common.util.JsonUtils;
import com.example.order.seckill.entity.SeckillEvent;
import com.example.order.seckill.mapper.SeckillOrderMapper;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RocketMQMessageListener(topic= SeckillConsumer.SECKILL_ORDER_TOPIC,
        consumerGroup= SeckillConsumer.SECKILL_ORDER_GROUP)
public class SeckillConsumer implements RocketMQListener<String> {

    public static final String SECKILL_ORDER_TOPIC = "SECKILL_ORDER_TOPIC";
    public static final String SECKILL_ORDER_GROUP = "seckill-order-group";

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Transactional
    public void onMessage(String msg) {

        SeckillEvent e = JsonUtils.fromJson(msg, SeckillEvent.class);

        try {
            seckillOrderMapper.insertSeckillFence(e.getSkuId(), e.getUserId());
            seckillOrderMapper.insertOrder(UUID.randomUUID().toString(), e.getUserId());
        } catch (DuplicateKeyException ignore) {}
    }
}
