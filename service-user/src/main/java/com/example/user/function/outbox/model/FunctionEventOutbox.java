package com.example.user.function.outbox.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("function_event_outbox")
public class FunctionEventOutbox {

    private Long id;
    private String eventType;
    private String eventKey;
    private String payload;

    private Integer status;
    private Integer retryCount;
    private LocalDateTime nextRetryTime;
    private String errorMsg;
}
