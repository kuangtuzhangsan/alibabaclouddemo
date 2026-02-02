package com.example.common.exception;

public enum ErrorCode {
    // ===== 通用 =====
    SUCCESS(0, "success"),
    PARAM_ERROR(1000, "参数错误"),
    SYSTEM_ERROR(1001, "系统异常"),

    // ===== 函数相关 =====
    FUNCTION_NOT_FOUND(2001, "函数不存在"),
    FUNCTION_VERSION_CONFLICT(2002, "函数版本冲突"),
    FUNCTION_COMPILE_ERROR(2003, "函数编译失败"),
    FUNCTION_EXECUTE_ERROR(2004, "函数执行失败"),

    // ====订单已存在====
    ORDER_EXIST(3001, "订单已存在"),

    //
    USER_NOT_EXIST(4001, "用户不存在");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
