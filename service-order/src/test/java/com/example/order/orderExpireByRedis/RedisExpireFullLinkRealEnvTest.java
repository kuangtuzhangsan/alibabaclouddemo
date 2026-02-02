package com.example.order.orderExpireByRedis;

import com.example.order.OrderApplication;
import com.example.order.orderExpireByRedis.entity.TOrder;
import com.example.order.orderExpireByRedis.mapper.TOrderExpireKeyMapper;
import com.example.order.orderExpireByRedis.mapper.TOrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {OrderApplication.class})
@ActiveProfiles("test")
class RedisExpireFullLinkRealEnvTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private TOrderMapper tOrderMapper;

    @Autowired
    private TOrderExpireKeyMapper expireKeyMapper;

    @Autowired
    RedisMessageListenerContainer container;

    @BeforeEach
    void startContainer() {
        container.start();   // 关键
    }

    @Test
    void redis_key_expire_should_close_order() throws Exception {

        Long orderId = 3001L;
        String orderNo = "NO3008";

        // 1. 插入未支付订单
        TOrder order = new TOrder();
        order.setOrderNo(orderNo);
        order.setUserId(orderId);
        order.setAmount(new BigDecimal(100.5));
        order.setStatus(OrderStatus.UNPAID.getCode());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        tOrderMapper.insert(order);

        // 等待订阅完成
        while (!container.isRunning()) {
            Thread.sleep(100);
        }

        System.out.println(container.isRunning());

        Thread.sleep(2000); // 再保险一点

        // 2. 设置Redis TTL Key
        String key = "order:ttl:" + orderId + "_" + orderNo;
        redisTemplate.opsForValue().set(key, "1", Duration.ofSeconds(5));

        Properties getConfig = redisTemplate.getConnectionFactory()
                .getConnection()
                .getConfig("notify-keyspace-events");

        Thread.sleep(3000);

        // 主动触发 Redis 过期检查
        redisTemplate.hasKey(key);

        Thread.sleep(3000);

        // 4. 验证订单被关闭
        TOrder tOrder = tOrderMapper.selectById(orderId);
        assertEquals(OrderStatus.CLOSED.getCode(), order.getStatus());

        // 5. 验证幂等表只一条
//        int count = expireKeyMapper.countByOrderId(orderId);
//        assertEquals(1, count);
    }
}