package com.example.user.function.dto;

import lombok.Data;

@Data
public class FunctionPublishRequest {

    private String functionCode;
    private String functionName;
    private String groovyScript;
    private Integer version;
    private String operator;
}

