package com.example.user.function.outbox.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FunctionEventOutbox {

    private Long id;
    private String eventType;
    private String eventKey;
    private String payload;
    private Integer status;
    private Integer retryCount;
    private LocalDateTime nextRetryTime;
}
