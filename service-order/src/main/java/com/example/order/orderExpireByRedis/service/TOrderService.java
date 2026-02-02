package com.example.order.orderExpireByRedis.service;

import com.example.common.exception.BizException;
import com.example.common.exception.ErrorCode;
import com.example.order.orderExpireByRedis.OrderStatus;
import com.example.order.orderExpireByRedis.entity.TOrder;
import com.example.order.orderExpireByRedis.listener.OrderExpireListener;
import com.example.order.orderExpireByRedis.mapper.TOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class TOrderService {

    @Autowired
    private TOrderMapper orderMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Transactional
    public String createOrder(Long userId, BigDecimal amount, String orderNo) {

        TOrder tOrder = orderMapper.selectByOrderNo(orderNo, userId);
        if (tOrder != null && tOrder.getOrderNo() != null) {
            throw new BizException(ErrorCode.ORDER_EXIST);
        }

        TOrder order = new TOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setAmount(amount);
        order.setStatus(OrderStatus.UNPAID.getCode());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.insert(order);

        // 设置过期Key
        redisTemplate.opsForValue().set(
                OrderExpireListener.ORDER_TTL_KEY + order.getId() + "_" + orderNo,
                order.getId().toString(),
                1,
                TimeUnit.MINUTES
        );

        return order.getId() + "_" + orderNo;
    }
}
