package com.example.user.function.controller;

import com.example.user.function.dto.FunctionExecuteRequest;
import com.example.user.function.execute.FunctionExecuteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/function")
public class FunctionController {

    @Autowired
    private FunctionExecuteService functionExecuteService;

    @PostMapping("/execute")
    public Object execute(@RequestBody FunctionExecuteRequest request) {
        return functionExecuteService.execute(
                request.getFunctionCode(),
                request.getParams()
        );
    }
}