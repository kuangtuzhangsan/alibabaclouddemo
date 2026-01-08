package com.example.user.function.controller;

import com.example.common.web.ApiResponse;
import com.example.user.function.cache.FunctionWrapper;
import com.example.user.function.dto.FunctionExecuteRequest;
import com.example.user.function.dto.FunctionPublishRequest;
import com.example.user.function.execute.FunctionExecuteService;
import com.example.user.function.service.FunctionPublishService;
import com.example.user.function.service.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/function")
public class FunctionController {

    @Autowired
    private FunctionExecuteService functionExecuteService;

    @Autowired
    private FunctionPublishService publishService;

    @Autowired
    private FunctionService functionService;

    @PostMapping("/execute")
    public ApiResponse<Object> execute(@RequestBody FunctionExecuteRequest request) {
        return ApiResponse.success(
                functionExecuteService.execute(
                        request.getFunctionCode(),
                        request.getParams()
        ));
    }

    @PostMapping("/publish")
    public ApiResponse publish(@RequestBody FunctionPublishRequest request) {
        publishService.publish(request);
        return ApiResponse.success();
    }

    @GetMapping("/{code}")
    public ApiResponse<FunctionWrapper> get(@PathVariable String code) {
        return ApiResponse.success(functionService.findActiveByCodeCache(code));
    }
}