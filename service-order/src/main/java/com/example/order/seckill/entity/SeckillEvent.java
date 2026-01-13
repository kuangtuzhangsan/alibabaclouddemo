package com.example.order.seckill.entity;

import lombok.Data;

@Data
public class SeckillEvent {

    private Long skuId;
    private Long userId;

    public SeckillEvent(Long skuId, Long userId) {
        this.skuId = skuId;
        this.userId = userId;
    }

    public SeckillEvent() {
    }
}
