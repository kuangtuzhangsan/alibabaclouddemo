package com.example.user.function.event;

import lombok.Data;

import java.io.Serializable;

@Data
public class FunctionCacheRefreshEvent implements Serializable {

    /**
     * 函数 code
     */
    private String functionCode;

    /**
     * 新版本号
     */
    private Long version;

    /**
     * 全量刷新 or 单函数刷新
     */
    private boolean fullRefresh;
}