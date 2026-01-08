package com.example.common.exception;

public class FunctionExecuteException extends FunctionException {

    public FunctionExecuteException(String functionCode, Throwable cause) {
        super(
                ErrorCode.FUNCTION_EXECUTE_ERROR,
                "Groovy execute failed: " + functionCode
        );
        initCause(cause);
    }
}
