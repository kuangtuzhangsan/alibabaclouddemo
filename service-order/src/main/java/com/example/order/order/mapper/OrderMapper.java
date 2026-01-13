package com.example.order.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.order.order.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<OrderInfo> {

    List<OrderInfo> selectExpiredOrders(
            @Param("expireTime") LocalDateTime expireTime
    );

    int cancelOrder(@Param("id") Long id);

    int insertOrder(@Param("orderNo") String orderNo,
                    @Param("userId") Long userId);
}

