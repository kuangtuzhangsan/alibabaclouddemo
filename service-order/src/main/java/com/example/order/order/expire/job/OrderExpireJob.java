package com.example.order.order.expire.job;

import com.example.order.order.expire.sevice.OrderExpireService;
import com.example.order.utils.nacos.NacosInstanceIdProvider;
import com.example.order.utils.redis.RedisLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class OrderExpireJob {

    private static final Logger log = LoggerFactory.getLogger(OrderExpireJob.class);

    private static final String LOCK_KEY = "order:expire:lock";

    @Autowired
    private RedisLockService lockService;

    @Autowired
    private OrderExpireService expireService;

    @Autowired
    private NacosInstanceIdProvider instanceIdProvider;

    @Scheduled(cron = "10 * * * * ?")
    public void execute() {

        String instanceId = instanceIdProvider.getInstanceId();
        log.info("instanceId="+instanceId);

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
