package com.example.order.orderExpireByRedis;

public enum OrderStatus {
    UNPAID(0),
    PAID(1),
    CLOSED(2);

    private final int code;
    OrderStatus(int code) { this.code = code; }
    public int getCode() { return code; }
}

