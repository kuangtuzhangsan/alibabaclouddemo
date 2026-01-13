package com.example.order.order.service;

import com.example.order.order.entity.OrderInfo;
import com.example.order.order.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderCreateService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Transactional
    public void createOrder(String orderNo) {

        OrderInfo order = new OrderInfo();
        order.setOrderNo(orderNo);
        order.setStatus(0);
        order.setCreateTime(LocalDateTime.now());

        orderMapper.insert(order);

        long expireTime = System.currentTimeMillis() + 15 * 60 * 1000;

        redisTemplate.opsForZSet().add(
                "order:expire:zset",
                order.getId().toString(),
                expireTime
        );
    }
}

