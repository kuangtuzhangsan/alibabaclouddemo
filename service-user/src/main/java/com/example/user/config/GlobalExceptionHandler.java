package com.example.user.config;

import com.example.common.exception.BaseBusinessException;
import com.example.common.exception.ErrorCode;
import com.example.common.web.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BaseBusinessException.class)
    public ApiResponse<?> handleBizException(BaseBusinessException e) {
        return ApiResponse.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e) {
        log.error("系统异常", e);
        return ApiResponse.fail(
                ErrorCode.SYSTEM_ERROR.getCode(),
//                ErrorCode.SYSTEM_ERROR.getMessage()
                e.getMessage()
        );
    }
}
