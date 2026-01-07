package com.example.user.function.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("function_publish_log")
public class FunctionPublishLog {

    private Long id;

    private String functionCode;

    private Integer version;

    private String operator;

    private String publishType;

    /**
     * 1-成功 0-失败
     */
    private Integer publishStatus;

    private String failReason;

    private LocalDateTime createdTime;
}
