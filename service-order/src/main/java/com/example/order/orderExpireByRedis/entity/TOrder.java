package com.example.order.orderExpireByRedis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_order")
public class TOrder {
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal amount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime closeTime;
    private LocalDateTime updateTime;
}
