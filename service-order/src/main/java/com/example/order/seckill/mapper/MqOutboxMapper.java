package com.example.order.seckill.mapper;

import com.example.order.seckill.entity.MqOutbox;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MqOutboxMapper {

    void insert(MqOutbox o);

    List<MqOutbox> selectUnsent(@Param("limit") int limit);

    void markSent(Long id);
}

