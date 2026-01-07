package com.example.user.function.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.function.model.FunctionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FunctionMapper extends BaseMapper<FunctionEntity> {

    FunctionEntity findActiveByCode(@Param("code") String code);
}
