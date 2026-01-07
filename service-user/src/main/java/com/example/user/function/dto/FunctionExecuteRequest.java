package com.example.user.function.dto;

import lombok.Data;

import java.util.Map;

@Data
public class FunctionExecuteRequest {

    private String functionCode;
    private Map<String, Object> params;
}
