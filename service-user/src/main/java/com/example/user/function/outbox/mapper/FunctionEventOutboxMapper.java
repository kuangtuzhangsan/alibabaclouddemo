package com.example.user.function.outbox.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.function.outbox.model.FunctionEventOutbox;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FunctionEventOutboxMapper extends BaseMapper<FunctionEventOutbox> {

    int insert(FunctionEventOutbox outbox);

    List<FunctionEventOutbox> selectPending(@Param("limit") int limit);

    List<FunctionEventOutbox> selectForSend(@Param("limit") int limit);

    void markSent(@Param("id") Long id);

    void increaseRetry(Long id, LocalDateTime nextRetryTime);

    int markFailed(@Param("id") Long id,
                   @Param("retryCount") int retryCount,
                   @Param("nextRetryTime") LocalDateTime nextRetryTime,
                   @Param("errorMsg") String errorMsg);
}
