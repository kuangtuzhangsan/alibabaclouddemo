package com.example.user.function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class FunctionExecuteThreadPoolConfig {

    @Bean("functionExecutor")
    public ExecutorService functionExecutor() {
        return new ThreadPoolExecutor(
                4,                      // core
                8,                      // max
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(200),
                r -> new Thread(r, "function-exec-thread"),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}

