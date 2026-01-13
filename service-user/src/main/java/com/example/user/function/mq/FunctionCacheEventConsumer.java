package com.example.user.function.mq;

import com.example.user.function.event.FunctionCacheRefreshEvent;
import com.example.user.function.outbox.NacosInstanceIdProvider;
import com.example.user.function.outbox.mapper.MQConsumeLogMapper;
import com.example.user.function.outbox.model.MQConsumeLog;
import com.example.user.function.service.FunctionRefreshService;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(FunctionCacheEventConsumer.class);

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

        try {
            if (event.isFullRefresh()) {
                refreshService.refreshAll();
            } else {
                refreshService.refresh(event.getFunctionCode(), event.getVersion());
            }

            // 记录消费日志
            consumeLogMapper.insert(
                    new MQConsumeLog(
                            FUNCTION_CACHE_REFRESH,
                            eventKey,
                            instanceId,
                            LocalDateTime.now()
                    )
            );

        } catch (Exception e) {
            log.error("处理缓存刷新事件失败: functionCode={}, version={}, instanceId={}",
                    event.getFunctionCode(), event.getVersion(), instanceId, e);
        }
    }
}