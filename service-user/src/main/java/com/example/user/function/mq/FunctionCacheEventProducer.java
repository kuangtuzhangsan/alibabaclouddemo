package com.example.user.function.mq;

import com.example.user.function.event.FunctionCacheRefreshEvent;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class FunctionCacheEventProducer {

    public static final String TOPIC = "FUNCTION_CACHE_REFRESH_TOPIC";

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void send(FunctionCacheRefreshEvent event) {

        String key =
                event.getFunctionCode() + "_" + event.getVersion();

        Message<?> msg = MessageBuilder
                .withPayload(event)
                .setHeader(RocketMQHeaders.KEYS, key)
                .build();

        rocketMQTemplate.syncSend(TOPIC, msg);
    }
}