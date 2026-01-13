package com.example.order.order.expire.mq;

import com.example.order.order.mapper.OrderMapper;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RocketMQMessageListener(
        topic = "ORDER_EXPIRE_TOPIC",
        consumerGroup = "order-expire-group"
)
public class OrderExpireConsumer
        implements RocketMQListener<String> {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Transactional
    public void onMessage(String orderId) {

        // 幂等更新
        orderMapper.cancelOrder(Long.valueOf(orderId));
    }
}
