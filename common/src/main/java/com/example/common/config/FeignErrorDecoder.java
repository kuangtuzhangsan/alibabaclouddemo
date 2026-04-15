package com.example.common.config;

import com.example.common.exception.RemoteServiceException;
import com.example.common.web.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Feign 错误解码器（公共模块）
 * 统一处理远程服务调用异常
 */
public class FeignErrorDecoder implements ErrorDecoder {

    private static final Logger log = LoggerFactory.getLogger(FeignErrorDecoder.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        String reason = response.reason() != null ? response.reason() : "Remote Service Error";

        try {
            if (response.body() != null) {
                String body = toString(response.body().asInputStream());
                ApiResponse<?> apiResponse = objectMapper.readValue(body, ApiResponse.class);
                int code = apiResponse.getCode() != 0 ? apiResponse.getCode() : status;
                String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : reason;
                
                log.warn("Feign调用异常: method={}, status={}, code={}, message={}", 
                        methodKey, status, code, message);
                
                return new RemoteServiceException(status, message, code);
            }
        } catch (IOException e) {
            log.error("Feign错误响应解析失败: method={}", methodKey, e);
        }

        return new RemoteServiceException(status, reason, status);
    }

    private String toString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
