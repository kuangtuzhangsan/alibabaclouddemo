package com.example.user.function.execute;

import com.example.common.exception.FunctionNotFoundException;
import com.example.common.util.JsonUtils;
import com.example.user.function.cache.FunctionWrapper;
import com.example.user.function.compile.GroovyCompiler;
import com.example.user.function.dao.FunctionMapper;
import com.example.user.function.event.FunctionCacheRefreshEvent;
import com.example.user.function.model.FunctionEntity;
import com.example.user.function.outbox.mapper.FunctionEventOutboxMapper;
import com.example.user.function.outbox.model.FunctionEventOutbox;
import com.example.user.function.outbox.service.FunctionEventOutboxService;
import com.github.benmanes.caffeine.cache.Cache;
import groovy.lang.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class FunctionExecuteService {

    @Autowired
    private Cache<String, FunctionWrapper> functionCache;

    @Autowired
    private FunctionMapper functionMapper;

    @Autowired
    private GroovyCompiler groovyCompiler;

    @Autowired
    private FunctionEventOutboxService outboxService;

    @Autowired
    @Qualifier("functionExecutor")
    private ExecutorService executor;

    @Autowired
    private FunctionEventOutboxMapper outboxMapper;

    /**
     * 执行函数（带超时 + 缓存修复）
     */
    @Transactional
    public Object execute(
            String functionCode,
            Map<String, Object> params
    ) throws FunctionNotFoundException {

        FunctionWrapper wrapper = functionCache.getIfPresent(functionCode);

        // ⭐ 缓存未命中，回源 DB
        if (wrapper == null) {
            wrapper = loadFromDbAndRefreshCache(functionCode);
        }

        if (wrapper == null) {
            throw new FunctionNotFoundException(functionCode);
        }

        FunctionWrapper finalWrapper = wrapper;

        Future<Object> future = executor.submit(() -> {

            Script script = finalWrapper.getScript();

            // 注入参数
            params.forEach(script::setProperty);

            return script.run();
        });

        try {
            // ⏱ 超时控制（例如 500ms）
            return future.get(500, TimeUnit.MILLISECONDS);

        } catch (TimeoutException e) {

            // ⚠️ 超时立刻中断
            future.cancel(true);

            throw new RuntimeException(
                    "Function execution timeout: " + functionCode, e
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Function execution failed: " + functionCode, e
            );
        }
    }

    /**
     * DB 回源 + 本地缓存修复 + MQ 通知
     */
    private FunctionWrapper loadFromDbAndRefreshCache(String functionCode) {

        FunctionEntity entity =
                functionMapper.findActiveByCode(functionCode);

        if (entity == null) {
            functionCache.invalidate(functionCode);
            return null;
        }

        Script script =
                groovyCompiler.compile(entity.getGroovyScript());

        FunctionWrapper wrapper = new FunctionWrapper(
                entity.getFunctionCode(),
                entity.getVersion(),
                script
        );

        // ⭐ 原子写缓存
        functionCache.put(functionCode, wrapper);

        // ⭐ 写 Outbox（非事务也 OK）
        outboxService.writeCacheRefreshEvent(
                entity.getFunctionCode(),
                entity.getVersion()
        );

        return wrapper;
    }

}


