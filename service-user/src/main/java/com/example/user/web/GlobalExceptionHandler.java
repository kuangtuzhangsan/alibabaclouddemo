package com.example.user.web;

import com.example.common.exception.BaseBusinessException;
import com.example.common.exception.ErrorCode;
import com.example.common.web.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseBusinessException.class)
    public ApiResponse<?> handleBizException(BaseBusinessException e) {
        return ApiResponse.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e) {
        return ApiResponse.fail(
                ErrorCode.SYSTEM_ERROR.getCode(),
                ErrorCode.SYSTEM_ERROR.getMessage()
        );
    }
}
