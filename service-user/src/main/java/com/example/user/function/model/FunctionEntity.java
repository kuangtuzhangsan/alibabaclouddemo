package com.example.user.function.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("function_definition")
public class FunctionEntity {

    private Long id;
    private String functionCode;
    private String functionName;
    private String groovyScript;
    private Long version;
    private Integer status;
}

