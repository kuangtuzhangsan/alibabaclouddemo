package com.example.order.orderExpireByRedis.listener;

import com.example.order.orderExpireByRedis.OrderStatus;
import com.example.order.orderExpireByRedis.entity.TOrderExpireKey;
import com.example.order.orderExpireByRedis.mapper.TOrderExpireKeyMapper;
import com.example.order.orderExpireByRedis.mapper.TOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class OrderExpireListener {

    public static final String ORDER_TTL_KEY = "order:ttl:";

    @Autowired
    private TOrderMapper orderMapper;

    @Autowired
    private TOrderExpireKeyMapper tOrderExpireKeyMapper;

    // 注意这里
    public void handleMessage(String expiredKey) {

        if (!expiredKey.startsWith(ORDER_TTL_KEY)) {
            return;
        }

//        Long orderId = Long.valueOf(expiredKey.substring(ORDER_TTL_KEY.length()));
        String value = expiredKey.substring(ORDER_TTL_KEY.length());
        Long orderId = Long.valueOf(value.split("_")[0]);
        String orderNo = value.split("_")[1];

        // 先插入本地记录表
        try {
            tOrderExpireKeyMapper.insertExpireKey(TOrderExpireKey.generateEntity(orderId, orderNo, expiredKey));
        } catch (DuplicateKeyException e) {
            // 特别处理唯一索引冲突
            log.debug("过期Key记录已存在，orderId={}, orderNo={}", orderId, orderNo);
            return;
        } catch (Exception e) {
            // 其他异常记录错误级别日志
            log.error("插入过期Key记录失败，orderId={}, orderNo={}", orderId, orderNo, e);
            return;
        }

        log.info("订单过期事件触发，orderId={}，orderNo={}", orderId, orderNo);

        // where status = 0 乐观锁
        int affected = orderMapper.closeIfUnpaid(
                orderId,
                OrderStatus.UNPAID.getCode(),
                orderNo,
                LocalDateTime.now()
        );

        if (affected == 1) {
            log.info("订单关闭成功 orderId={}，orderNo={}", orderId, orderNo);
        } else {
            log.info("订单已支付或已关闭 orderId={}，orderNo={}", orderId, orderNo);
        }
    }
}