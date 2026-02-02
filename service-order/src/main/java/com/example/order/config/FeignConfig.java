package com.example.order.config;

import com.example.common.exception.RemoteServiceException;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FeignConfig {

    /**
     * 捕获服务端返回的非 2xx 响应
     * 并抛出自定义异常
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}