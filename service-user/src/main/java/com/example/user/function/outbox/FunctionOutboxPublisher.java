package com.example.user.function.outbox;

import com.example.common.util.JsonUtils;
import com.example.user.function.event.FunctionCacheRefreshEvent;
import com.example.user.function.mq.FunctionCacheEventProducer;
import com.example.user.function.outbox.mapper.FunctionEventOutboxMapper;
import com.example.user.function.outbox.model.FunctionEventOutbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class FunctionOutboxPublisher {

    @Autowired
    private FunctionEventOutboxMapper outboxMapper;

    @Autowired
    private FunctionCacheEventProducer producer;

    @Scheduled(fixedDelay = 3000)
    public void publish() {

        List<FunctionEventOutbox> events =
                outboxMapper.selectPending(50);

        for (FunctionEventOutbox event : events) {
            try {
                FunctionCacheRefreshEvent payload =
                        JsonUtils.fromJson(
                                event.getPayload(),
                                FunctionCacheRefreshEvent.class
                        );

                producer.send(payload);

                outboxMapper.markSent(event.getId());

            } catch (Exception e) {

                // 指数退避（简单版）
                LocalDateTime nextRetry =
                        LocalDateTime.now().plusSeconds(
                                Math.min(60, 1 << event.getRetryCount())
                        );

                outboxMapper.increaseRetry(event.getId(), nextRetry);
            }
        }
    }
}
