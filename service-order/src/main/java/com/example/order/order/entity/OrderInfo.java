package com.example.order.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("order_info")
public class OrderInfo {

    private Long id;
    private String orderNo;
    private Long userId;
    private Integer status;
    private LocalDateTime createTime;
}



