package com.example.common.web;

import java.io.Serializable;

public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务状态码
     * 0 表示成功，其它表示失败
     */
    private int code;

    /**
     * 描述信息
     */
    private String message;

    /**
     * 业务数据
     */
    private T data;

    // ===== 构造器私有化 =====

    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ===== 成功响应 =====

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(0, "success", null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "success", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(0, message, data);
    }

    // ===== 失败响应 =====

    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public static <T> ApiResponse<T> fail(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    // ===== getter / setter =====

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}