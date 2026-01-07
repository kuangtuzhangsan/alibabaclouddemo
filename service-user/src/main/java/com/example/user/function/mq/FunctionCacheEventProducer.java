package com.example.user.function.mq;

import com.example.user.function.event.FunctionCacheRefreshEvent;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FunctionCacheEventProducer {

    public static final String TOPIC = "FUNCTION_CACHE_REFRESH";

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void send(FunctionCacheRefreshEvent event) {
        rocketMQTemplate.convertAndSend(TOPIC, event);
    }
}