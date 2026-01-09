package com.example.user.function.outbox.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mq_consume_log")
public class MQConsumeLog {

    private String eventType;
    private String eventKey;
    private  String instanceId;
    private LocalDateTime consumedTime;

    public MQConsumeLog(String eventType, String eventKey, String instanceId, LocalDateTime consumedTime) {
        this.eventType = eventType;
        this.eventKey = eventKey;
        this.instanceId = instanceId;
        this.consumedTime = consumedTime;
    }

    public MQConsumeLog() {
    }
}
