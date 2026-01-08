package com.example.common.exception;

public class FunctionNotFoundException extends FunctionException {

    public FunctionNotFoundException(String functionCode) {
        super(
                ErrorCode.FUNCTION_NOT_FOUND,
                "Function not found: " + functionCode
        );
    }
}
