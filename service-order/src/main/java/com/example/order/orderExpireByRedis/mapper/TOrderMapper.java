package com.example.order.orderExpireByRedis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.order.orderExpireByRedis.entity.TOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface TOrderMapper extends BaseMapper<TOrder> {

//    void insert(TOrder order);

    TOrder selectById(Long id);

    TOrder selectByOrderNo(@Param("orderNo")String orderNo,
                           @Param("userId") Long userId);

    int updateStatus(@Param("id") Long id,
                     @Param("status") Integer status,
                     @Param("updateTime") LocalDateTime updateTime);

    int closeIfUnpaid(@Param("id") Long id,
                      @Param("status") Integer status,
                      @Param("orderNo") String orderNo,
                      @Param("closeTime") LocalDateTime closeTime);
}
