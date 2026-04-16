package com.example.user.function.controller;

import com.example.common.web.ApiResponse;
import com.example.user.function.cache.FunctionWrapper;
import com.example.user.function.dto.FunctionExecuteRequest;
import com.example.user.function.dto.FunctionPublishRequest;
import com.example.user.function.execute.FunctionExecuteService;
import com.example.user.function.service.FunctionPublishService;
import com.example.user.function.service.FunctionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 函数控制器
 */
@Slf4j
@RestController
@RequestMapping("/function")
public class FunctionController {

    @Autowired
    private FunctionExecuteService functionExecuteService;

    @Autowired
    private FunctionPublishService publishService;

    @Autowired
    private FunctionService functionService;

    /**
     * 执行函数
     * @param request 函数执行请求
     * @return 执行结果
     */
    @PostMapping("/execute")
    public ApiResponse<Object> execute(@Valid @RequestBody FunctionExecuteRequest request) {
        log.info("执行函数请求 - 函数代码: {}", request.getFunctionCode());
        
        try {
            Object result = functionExecuteService.execute(
                    request.getFunctionCode(),
                    request.getParams()
            );
            log.debug("函数执行成功 - 函数代码: {}, 结果: {}", request.getFunctionCode(), result);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("函数执行失败 - 函数代码: {}", request.getFunctionCode(), e);
            return ApiResponse.error("函数执行失败: " + e.getMessage());
        }
    }

    /**
     * 发布函数
     * @param request 函数发布请求
     * @return 发布结果
     */
    @PostMapping("/publish")
    public ApiResponse<Void> publish(@Valid @RequestBody FunctionPublishRequest request) {
        log.info("发布函数请求 - 函数代码: {}", request.getCode());
        
        try {
            publishService.publish(request);
            log.info("函数发布成功 - 函数代码: {}", request.getCode());
            return ApiResponse.success("函数发布成功");
        } catch (Exception e) {
            log.error("函数发布失败 - 函数代码: {}", request.getCode(), e);
            return ApiResponse.error("函数发布失败: " + e.getMessage());
        }
    }

    /**
     * 获取函数信息
     * @param code 函数代码
     * @return 函数信息
     */
    @GetMapping("/{code}")
    public ApiResponse<FunctionWrapper> getFunction(@PathVariable String code) {
        log.info("获取函数信息 - 函数代码: {}", code);
        
        if (code == null || code.trim().isEmpty()) {
            return ApiResponse.error("函数代码不能为空");
        }
        
        try {
            FunctionWrapper function = functionService.findActiveByCodeCache(code);
            if (function == null) {
                return ApiResponse.error("函数不存在: " + code);
            }
            log.debug("获取函数信息成功 - 函数代码: {}, 函数: {}", code, function);
            return ApiResponse.success(function);
        } catch (Exception e) {
            log.error("获取函数信息失败 - 函数代码: {}", code, e);
            return ApiResponse.error("获取函数信息失败: " + e.getMessage());
        }
    }
}