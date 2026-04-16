package com.example.common.config;

import com.example.common.exception.BaseBusinessException;
import com.example.common.exception.ErrorCode;
import com.example.common.web.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器（公共模块）
 * 所有微服务继承使用，避免重复定义
 * 
 * 功能：
 * 1. 统一异常处理，避免敏感信息泄露
 * 2. 统一响应格式
 * 3. 支持参数校验异常
 * 4. 支持HTTP状态码映射
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 业务异常处理
     */
    @ExceptionHandler(BaseBusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleBizException(BaseBusinessException e, HttpServletRequest request) {
        log.warn("业务异常 - URI: {}, Code: {}, Message: {}", 
                request.getRequestURI(), e.getCode(), e.getMessage());
        return ApiResponse.fail(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常 - MethodArgumentNotValidException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        
        log.warn("参数校验失败 - URI: {}, 错误: {}", request.getRequestURI(), errorMessage);
        return ApiResponse.fail(ErrorCode.PARAM_ERROR.getCode(), errorMessage);
    }
    
    /**
     * 参数校验异常 - BindException
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleBindException(BindException e, HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        
        log.warn("参数绑定失败 - URI: {}, 错误: {}", request.getRequestURI(), errorMessage);
        return ApiResponse.fail(ErrorCode.PARAM_ERROR.getCode(), errorMessage);
    }
    
    /**
     * 参数校验异常 - ConstraintViolationException
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        
        log.warn("约束校验失败 - URI: {}, 错误: {}", request.getRequestURI(), errorMessage);
        return ApiResponse.fail(ErrorCode.PARAM_ERROR.getCode(), errorMessage);
    }
    
    /**
     * 缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        String errorMessage = String.format("缺少必要参数: %s", e.getParameterName());
        log.warn("缺少请求参数 - URI: {}, 参数: {}", request.getRequestURI(), e.getParameterName());
        return ApiResponse.fail(ErrorCode.PARAM_ERROR.getCode(), errorMessage);
    }
    
    /**
     * 参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String errorMessage = String.format("参数类型错误: %s 应为 %s 类型", 
                e.getName(), e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知");
        log.warn("参数类型不匹配 - URI: {}, 参数: {}, 错误: {}", 
                request.getRequestURI(), e.getName(), errorMessage);
        return ApiResponse.fail(ErrorCode.PARAM_ERROR.getCode(), errorMessage);
    }
    
    /**
     * 404异常处理
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<?> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("接口不存在 - URI: {}, Method: {}", request.getRequestURI(), request.getMethod());
        return ApiResponse.fail(HttpStatus.NOT_FOUND.value(), "接口不存在");
    }
    
    /**
     * 非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数 - URI: {}, 错误: {}", request.getRequestURI(), e.getMessage());
        return ApiResponse.fail(ErrorCode.PARAM_ERROR.getCode(), e.getMessage());
    }
    
    /**
     * 非法状态异常
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleIllegalStateException(IllegalStateException e, HttpServletRequest request) {
        log.warn("非法状态 - URI: {}, 错误: {}", request.getRequestURI(), e.getMessage());
        return ApiResponse.fail(ErrorCode.PARAM_ERROR.getCode(), e.getMessage());
    }

    /**
     * 系统异常 - 兜底处理
     * 注意：不返回详细堆栈信息，避免敏感信息泄露
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleException(Exception e, HttpServletRequest request) {
        // 记录完整异常信息到日志（仅服务端可见）
        log.error("系统异常 - URI: {}, Method: {}", request.getRequestURI(), request.getMethod(), e);
        
        // 返回给客户端的消息不包含详细异常信息
        String clientMessage = "系统内部错误，请稍后重试";
        
        // 开发环境可以返回更多信息（通过环境变量控制）
        if ("dev".equals(System.getenv("SPRING_PROFILES_ACTIVE"))) {
            clientMessage = e.getMessage();
        }
        
        return ApiResponse.fail(
                ErrorCode.SYSTEM_ERROR.getCode(),
                clientMessage
        );
    }
}
