package com.example.user.function.outbox;

import com.example.common.util.JsonUtils;
import com.example.user.function.event.FunctionCacheRefreshEvent;
import com.example.user.function.mq.FunctionCacheEventProducer;
import com.example.user.function.outbox.mapper.FunctionEventOutboxMapper;
import com.example.user.function.outbox.model.FunctionEventOutbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OutboxDispatcher {

    private static final Logger log = LoggerFactory.getLogger(OutboxDispatcher.class);

    @Autowired
    private FunctionEventOutboxMapper outboxMapper;

    @Autowired
    private FunctionCacheEventProducer producer;

    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void dispatch() {

        List<FunctionEventOutbox> list =
                outboxMapper.selectForSend(50);

        log.info("Dispatching outbox, size={}", list.size());

        for (FunctionEventOutbox outbox : list) {
            processOne(outbox);
        }
    }

    private void processOne(FunctionEventOutbox outbox) {

        try {
            FunctionCacheRefreshEvent event =
                    JsonUtils.fromJson(
                            outbox.getPayload(),
                            FunctionCacheRefreshEvent.class
                    );

            producer.send(event);

            outboxMapper.markSent(outbox.getId());

        } catch (Exception e) {

            outboxMapper.markFailed(
                    outbox.getId(),
                    outbox.getRetryCount() + 1,
                    LocalDateTime.now().plusSeconds(10),
                    e.getMessage()
            );
        }
    }
}
