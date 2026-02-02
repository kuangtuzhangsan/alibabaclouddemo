package com.example.order.orderExpireByRedis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.order.orderExpireByRedis.entity.TOrderExpireKey;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TOrderExpireKeyMapper extends BaseMapper<TOrderExpireKey> {

    int insertExpireKey(TOrderExpireKey entity);
}
