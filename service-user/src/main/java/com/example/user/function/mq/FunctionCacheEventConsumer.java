package com.example.user.function.mq;

import com.example.user.function.event.FunctionCacheRefreshEvent;
import com.example.user.function.outbox.NacosInstanceIdProvider;
import com.example.user.function.outbox.mapper.MQConsumeLogMapper;
import com.example.user.function.outbox.model.MQConsumeLog;
import com.example.user.function.service.FunctionRefreshService;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RocketMQMessageListener(
        topic = FunctionCacheEventProducer.TOPIC,
        consumerGroup = "function-cache-consumer",
        messageModel = MessageModel.BROADCASTING
)
public class FunctionCacheEventConsumer
        implements RocketMQListener<FunctionCacheRefreshEvent> {

    public static final String FUNCTION_CACHE_REFRESH = "FUNCTION_CACHE_REFRESH";

    @Autowired
    private MQConsumeLogMapper consumeLogMapper;

    @Autowired
    private FunctionRefreshService refreshService;

    @Autowired
    private NacosInstanceIdProvider nacosInstanceIdProvider;

    @Override
    public void onMessage(FunctionCacheRefreshEvent event) {

        String eventKey =
                event.getFunctionCode() + "_" + event.getVersion();

        String instanceId = nacosInstanceIdProvider.getInstanceId();

        if (consumeLogMapper.exists(
                FUNCTION_CACHE_REFRESH, eventKey, instanceId) > 0) {
            return;
        }

        if (event.isFullRefresh()) {
            refreshService.refreshAll();
        } else {
            refreshService.refresh(event.getFunctionCode(), event.getVersion());
        }

        consumeLogMapper.insert(
                new MQConsumeLog(
                        FUNCTION_CACHE_REFRESH,
                        eventKey,
                        instanceId,
                        LocalDateTime.now()
                )
        );
    }
}