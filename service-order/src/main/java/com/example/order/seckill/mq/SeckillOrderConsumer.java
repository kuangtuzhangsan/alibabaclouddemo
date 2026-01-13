package com.example.order.seckill.mq;

import com.example.order.seckill.mapper.SeckillOrderMapper;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DuplicateKeyException;

import java.util.UUID;

@Component
@RocketMQMessageListener(topic = "SECKILL_ORDER_TOPIC", consumerGroup = "seckill-order-group")
public class SeckillOrderConsumer implements RocketMQListener<String> {

    @Autowired
    private SeckillOrderMapper orderMapper;

    @Override
    @Transactional
    public void onMessage(String msg) {
        // msg format: skuId,userId
        String[] arr = msg.split(",");
        Long skuId = Long.valueOf(arr[0]);
        Long userId = Long.valueOf(arr[1]);

        try {
            // 1) 幂等 fence 插入：若重复会抛 DuplicateKeyException
            orderMapper.insertSeckillFence(skuId, userId);

            // 2) 插入真实订单
            orderMapper.insertOrder(UUID.randomUUID().toString(), userId);
        } catch (DuplicateKeyException e) {
            // 已经存在 fence，说明已经下过单，幂等：忽略
        }
    }
}


