package com.example.user.config;

import com.example.common.exception.RemoteServiceException;
import com.example.common.web.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        String reason = response.reason() != null ? response.reason() : "Remote Service Error";

        try {
            if (response.body() != null) {
                String body = toString(response.body().asInputStream());
                // 解析 ApiResponse
                ApiResponse<?> apiResponse = objectMapper.readValue(body, ApiResponse.class);
                int code = apiResponse.getCode() != 0 ? apiResponse.getCode() : status;
                String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : reason;
                return new RemoteServiceException(status, message, code);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 如果解析失败，使用 HTTP 状态码作为业务 code
        return new RemoteServiceException(status, reason, status);
    }

    // JDK 8 兼容 InputStream -> String
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
