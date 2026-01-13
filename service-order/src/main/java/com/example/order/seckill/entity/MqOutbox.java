package com.example.order.seckill.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MqOutbox {
    private Long id;
    private String topic;
    private String msgKey;
    private String payload ;
    private int status;
    private LocalDateTime createTime;
}
