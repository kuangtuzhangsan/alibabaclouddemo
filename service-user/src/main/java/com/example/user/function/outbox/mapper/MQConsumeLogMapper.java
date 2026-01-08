package com.example.user.function.outbox.mapper;

import com.example.user.function.outbox.model.MQConsumeLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MQConsumeLogMapper {

    int insert(MQConsumeLog log);

    int exists(@Param("eventType") String eventType,
               @Param("eventKey") String eventKey);
}

