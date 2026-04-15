package com.example.user.function.execute;

import com.example.common.exception.FunctionExecuteException;
import com.example.common.exception.FunctionNotFoundException;
import com.example.user.function.cache.FunctionWrapper;
import com.example.user.function.compile.GroovyCompiler;
import com.example.user.function.dao.FunctionMapper;
import com.example.user.function.model.FunctionEntity;
import com.example.user.function.outbox.service.FunctionEventOutboxService;
import com.github.benmanes.caffeine.cache.Cache;
import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 函数执行服务
 * 支持动态 Groovy 脚本执行、超时控制、缓存修复
 */
@Service
public class FunctionExecuteService {

    private static final Logger log = LoggerFactory.getLogger(FunctionExecuteService.class);

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

    @Value("${function.execute.timeout-ms:500}")
    private long timeoutMs;

    /**
     * 执行函数（带超时 + 缓存修复）
     */
    @Transactional
    public Object execute(String functionCode, Map<String, Object> params) {
        
        FunctionWrapper wrapper = getOrLoadFunction(functionCode);

        if (wrapper == null) {
            throw new FunctionNotFoundException(functionCode);
        }

        return executeWithTimeout(wrapper, params, functionCode);
    }

    /**
     * 获取函数（优先缓存，缓存未命中回源 DB）
     */
    private FunctionWrapper getOrLoadFunction(String functionCode) {
        FunctionWrapper wrapper = functionCache.getIfPresent(functionCode);
        
        if (wrapper == null) {
            log.debug("缓存未命中，回源加载函数: {}", functionCode);
            wrapper = loadFromDbAndRefreshCache(functionCode);
        }
        
        return wrapper;
    }

    /**
     * 带超时控制的函数执行
     */
    private Object executeWithTimeout(FunctionWrapper wrapper, Map<String, Object> params, String functionCode) {
        Future<Object> future = executor.submit(() -> {
            Script script = wrapper.getScript();
            params.forEach(script::setProperty);
            return script.run();
        });

        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);

        } catch (TimeoutException e) {
            future.cancel(true);
            log.warn("函数执行超时: functionCode={}, timeout={}ms", functionCode, timeoutMs);
            throw new FunctionExecuteException("Function execution timeout: " + functionCode, e);

        } catch (Exception e) {
            log.error("函数执行失败: functionCode={}", functionCode, e);
            throw new FunctionExecuteException("Function execution failed: " + functionCode, e);
        }
    }

    /**
     * DB 回源 + 本地缓存修复 + MQ 通知
     */
    private FunctionWrapper loadFromDbAndRefreshCache(String functionCode) {
        FunctionEntity entity = functionMapper.findActiveByCode(functionCode);

        if (entity == null) {
            functionCache.invalidate(functionCode);
            return null;
        }

        Script script = groovyCompiler.compile(entity.getGroovyScript());
        FunctionWrapper wrapper = new FunctionWrapper(
                entity.getFunctionCode(),
                entity.getVersion(),
                script
        );

        functionCache.put(functionCode, wrapper);
        
        outboxService.writeCacheRefreshEvent(
                entity.getFunctionCode(),
                entity.getVersion()
        );

        log.debug("函数已加载到缓存: code={}, version={}", 
                entity.getFunctionCode(), entity.getVersion());

        return wrapper;
    }
}
