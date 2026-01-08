package com.example.common.exception;

public abstract class BaseBusinessException extends RuntimeException {

    private final int code;
    private final String message;

    protected BaseBusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    protected BaseBusinessException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.code = errorCode.getCode();
        this.message = detailMessage;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
