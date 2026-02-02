package com.example.order.order.expire.scanner;

import com.example.order.utils.nacos.NacosInstanceIdProvider;
import com.example.order.utils.redis.RedisLockService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@EnableScheduling
public class OrderExpireScanner {

    private static final String ZSET_KEY = "order:expire:zset";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private RedisLockService lockService;

    @Autowired
    private NacosInstanceIdProvider instanceIdProvider;

    @Scheduled(cron = "20 * * * * ?")
    public void scan() {

        String instanceId = instanceIdProvider.getInstanceId();

        boolean locked = lockService.tryLock("order:expire:scan", instanceId, 5);
        if (!locked) {
            return;
        }

        try {
            long now = System.currentTimeMillis();

            Set<String> orderIds =
                    redisTemplate.opsForZSet()
                            .rangeByScore(ZSET_KEY, 0, now, 0, 100);

            if (orderIds == null || orderIds.isEmpty()) {
                return;
            }

            for (String orderId : orderIds) {

                // 发 MQ
                rocketMQTemplate.convertAndSend(
                        "ORDER_EXPIRE_TOPIC",
                        orderId
                );

                // 从延迟队列移除
                redisTemplate.opsForZSet().remove(ZSET_KEY, orderId);
            }

        } finally {
            lockService.unlock("order:expire:scan", instanceId);
        }
    }
}

