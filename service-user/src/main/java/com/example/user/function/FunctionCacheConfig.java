package com.example.user.function;

import com.example.user.function.cache.FunctionWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FunctionCacheConfig {

    @Bean
    public Cache<String, FunctionWrapper> functionCache() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .build();
    }
}