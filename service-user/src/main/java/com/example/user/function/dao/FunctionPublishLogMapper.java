package com.example.user.function.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.function.model.FunctionPublishLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FunctionPublishLogMapper
        extends BaseMapper<FunctionPublishLog> {

    // 当前阶段：BaseMapper 已足够
    // 后续如果需要查询审计记录，再加方法
}