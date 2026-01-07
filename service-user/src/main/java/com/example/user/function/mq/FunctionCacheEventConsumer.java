package com.example.user.function.mq;

import com.example.user.function.event.FunctionCacheRefreshEvent;
import com.example.user.function.service.FunctionRefreshService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
        topic = FunctionCacheEventProducer.TOPIC,
        consumerGroup = "function-cache-consumer"
)
public class FunctionCacheEventConsumer
        implements RocketMQListener<FunctionCacheRefreshEvent> {

    @Autowired
    private FunctionRefreshService refreshService;

    @Override
    public void onMessage(FunctionCacheRefreshEvent event) {

        if (event.isFullRefresh()) {
            refreshService.refreshAll();
        } else {
            refreshService.refresh(event.getFunctionCode(), event.getVersion());
        }
    }
}