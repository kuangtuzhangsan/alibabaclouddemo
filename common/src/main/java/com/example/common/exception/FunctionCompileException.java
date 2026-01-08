package com.example.common.exception;

public class FunctionCompileException extends FunctionException {

    public FunctionCompileException(String functionCode, Throwable cause) {
        super(
                ErrorCode.FUNCTION_COMPILE_ERROR,
                "Groovy compile failed: " + functionCode
        );
        initCause(cause);
    }
}