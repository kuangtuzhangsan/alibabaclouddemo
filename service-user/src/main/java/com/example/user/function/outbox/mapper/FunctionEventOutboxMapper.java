package com.example.user.function.outbox.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.function.outbox.model.FunctionEventOutbox;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FunctionEventOutboxMapper extends BaseMapper<FunctionEventOutbox> {

    int insert(FunctionEventOutbox outbox);

    List<FunctionEventOutbox> selectPending(int limit);

    void markSent(Long id);

    void increaseRetry(Long id, LocalDateTime nextRetryTime);
}
