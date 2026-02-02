package com.example.order.orderExpireByRedis.service;

import com.example.order.orderExpireByRedis.OrderStatus;
import com.example.order.orderExpireByRedis.mapper.TOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private static final String ORDER_TTL_KEY = "order:ttl:";

    @Autowired
    private TOrderMapper orderMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Transactional
    public void pay(Long orderId) {
        int updated = orderMapper.updateStatus(
                orderId,
                OrderStatus.PAID.getCode(),
                LocalDateTime.now()
        );

        if (updated == 1) {
            // 删除 Key，防止过期事件触发
            redisTemplate.delete(ORDER_TTL_KEY + orderId);
        }
    }
}
