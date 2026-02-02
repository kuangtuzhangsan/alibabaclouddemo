package com.example.order.orderExpireByRedis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class TOrderExpireKey {

    @TableId(type = IdType.ASSIGN_ID) // 使用雪花算法自动生成ID
    private Long id; // 表id

    private Long orderId; // 订单id

    private String orderNo; // 订单编号

    private String redisKey; // 订单订阅key

    private String status;//定长字符做状态 0-未处理 1-处理中 2-处理完成

    // orderId + orderNo 做唯一索引

    public static TOrderExpireKey generateEntity(Long orderId, String orderNo, String key) {
        TOrderExpireKey entity = new TOrderExpireKey();
        entity.setOrderId(orderId);
        entity.setOrderNo(orderNo);
        entity.setRedisKey(key);
        entity.setStatus("0");

        return entity;
    }
}

/**
 * CREATE TABLE t_order_expire_key (
 *     id           BIGINT PRIMARY KEY COMMENT '雪花ID',
 *
 *     order_id     BIGINT NOT NULL COMMENT '订单ID',
 *     order_no     VARCHAR(64) NOT NULL COMMENT '订单号',
 *
 *     redis_key    VARCHAR(128) NOT NULL COMMENT 'Redis 订阅Key',
 *
 *     status       CHAR(1) NOT NULL DEFAULT '0' COMMENT '0-未处理 1-处理中 2-已完成',
 *
 *     retry_count  INT NOT NULL DEFAULT 0 COMMENT '重试次数',
 *
 *     next_retry_time DATETIME DEFAULT NULL COMMENT '下次重试时间',
 *
 *     create_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 *     update_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 *
 *     UNIQUE KEY uk_order (order_id, order_no),
 *     UNIQUE KEY uk_redis_key (redis_key),
 *     KEY idx_status_retry (status, next_retry_time)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单过期关闭任务表';
 */
