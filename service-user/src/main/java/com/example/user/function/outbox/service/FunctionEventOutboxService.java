package com.example.user.function.outbox.service;

import com.example.common.util.JsonUtils;
import com.example.user.function.event.FunctionCacheRefreshEvent;
import com.example.user.function.mq.FunctionCacheEventConsumer;
import com.example.user.function.outbox.mapper.FunctionEventOutboxMapper;
import com.example.user.function.outbox.model.FunctionEventOutbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FunctionEventOutboxService {

    @Autowired
    private FunctionEventOutboxMapper outboxMapper;

    @Transactional
    public void writeCacheRefreshEvent(
            String functionCode,
            Long version
    ) {

        FunctionCacheRefreshEvent event =
                new FunctionCacheRefreshEvent();
        event.setFunctionCode(functionCode);
        event.setVersion(version);
        event.setFullRefresh(false);

        FunctionEventOutbox outbox = new FunctionEventOutbox();
        outbox.setEventType(FunctionCacheEventConsumer.FUNCTION_CACHE_REFRESH);
        outbox.setEventKey(functionCode + "_" + version);
        outbox.setPayload(JsonUtils.toJson(event));

        try {
            outboxMapper.insert(outbox);
        } catch (DuplicateKeyException e) {
            // 幂等：忽略
        }
    }

}
