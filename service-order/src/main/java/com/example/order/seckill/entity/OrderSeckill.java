package com.example.order.seckill.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderSeckill {
    private Long id;
    private Long skuId;
    private Long userId;
    private LocalDateTime createTime;
}
