package com.example.order.order.expire.job;

import com.example.order.order.expire.sevice.OrderExpireService;
import com.example.order.utils.redis.RedisLockService;
import com.example.user.function.outbox.NacosInstanceIdProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class OrderExpireJob {

    private static final String LOCK_KEY = "order:expire:lock";

    @Autowired
    private RedisLockService lockService;

    @Autowired
    private OrderExpireService expireService;

    @Autowired
    private NacosInstanceIdProvider instanceIdProvider;

    @Scheduled(cron = "0 */1 * * * ?")
    public void execute() {

        String instanceId = instanceIdProvider.getInstanceId();

        boolean locked = lockService.tryLock(
                LOCK_KEY,
                instanceId,
                55
        );

        if (!locked) {
            return;
        }

        try {
            expireService.expireOrders();
        } finally {
            lockService.unlock(LOCK_KEY, instanceId);
        }
    }
}
