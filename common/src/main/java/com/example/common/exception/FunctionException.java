package com.example.common.exception;

public class FunctionException extends BaseBusinessException {

    public FunctionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FunctionException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
