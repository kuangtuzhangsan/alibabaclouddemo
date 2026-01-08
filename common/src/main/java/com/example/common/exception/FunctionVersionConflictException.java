package com.example.common.exception;

public class FunctionVersionConflictException extends FunctionException {

    public FunctionVersionConflictException(String functionCode, int expect, int actual) {
        super(
                ErrorCode.FUNCTION_VERSION_CONFLICT,
                String.format(
                        "Function[%s] version conflict, expect=%d, actual=%d",
                        functionCode, expect, actual
                )
        );
    }
}
