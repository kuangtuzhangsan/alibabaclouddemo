package com.example.common.exception;

public class RemoteServiceException extends RuntimeException {
    private final int status;
    private final int code;

    public RemoteServiceException(int status, String message, int code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public int getStatus() { return status; }
    public int getCode() { return code; }
}
