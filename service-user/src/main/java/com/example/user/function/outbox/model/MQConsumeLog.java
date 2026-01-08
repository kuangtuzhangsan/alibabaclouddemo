package com.example.user.function.outbox.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mq_consume_log")
public class MQConsumeLog {

    private String eventType;
    private String eventKey;
    private LocalDateTime consumedTime;


    public MQConsumeLog(String eventType, String eventKey, LocalDateTime consumedTime) {
        this.eventType = eventType;
        this.eventKey = eventKey;
        this.consumedTime = consumedTime;
    }

    public MQConsumeLog() {
    }
}
