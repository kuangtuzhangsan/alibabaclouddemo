package com.example.user.function.execute;

import com.example.user.function.cache.FunctionWrapper;
import com.example.user.function.service.FunctionRefreshService;
import com.github.benmanes.caffeine.cache.Cache;
import groovy.lang.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
    @Qualifier("functionExecutor")
    private ExecutorService executor;

    /**
     * 执行函数（带超时）
     */
    public Object execute(
            String functionCode,
            Map<String, Object> params
    ) {

        FunctionWrapper wrapper = functionCache.getIfPresent(functionCode);
        if (wrapper == null) {
            throw new RuntimeException("Function not found: " + functionCode);
        }

        Future<Object> future = executor.submit(() -> {

            Script script = wrapper.getScript();

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
}

