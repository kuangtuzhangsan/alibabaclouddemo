package com.example.order.seckill.outbox;

import com.example.order.seckill.entity.MqOutbox;
import com.example.order.seckill.mapper.MqOutboxMapper;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxRelayJob {

    @Autowired
    private MqOutboxMapper outboxMapper;
    @Autowired
    private RocketMQTemplate mq;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void relay() {
        List<MqOutbox> list = outboxMapper.selectUnsent(100);

        for (MqOutbox o : list) {
            try {
                mq.syncSend(o.getTopic(), o.getPayload());
                outboxMapper.markSent(o.getId());
            } catch (Exception ignore) {}
        }
    }
}

