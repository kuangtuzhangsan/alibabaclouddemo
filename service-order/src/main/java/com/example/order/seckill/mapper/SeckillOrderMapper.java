package com.example.order.seckill.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SeckillOrderMapper {

    /**
     * 插入幂等 fence 记录（用于防重复下单）
     * 可能会抛 DuplicateKeyException 表示重复
     */
    int insertSeckillFence(@Param("skuId") Long skuId,
                           @Param("userId") Long userId);

    /**
     * 插入订单主表
     */
    int insertOrder(@Param("orderNo") String orderNo,
                    @Param("userId") Long userId);
}
