package com.example.order.demo.controller;

import com.example.common.entity.User;
import com.example.common.exception.ErrorCode;
import com.example.common.web.ApiResponse;
import com.example.order.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 * 演示用户相关操作
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        log.info("查询用户信息, ID: {}", id);
        
        if (id == null || id <= 0) {
            return ApiResponse.fail(ErrorCode.PARAM_ERROR.getCode(), "用户ID不能为空且必须大于0");
        }
        
        try {
            User user = userService.getById(id);
            if (user == null) {
                return ApiResponse.fail(ErrorCode.USER_NOT_EXIST.getCode(), ErrorCode.USER_NOT_EXIST.getMessage());
            }
            return ApiResponse.success(user);
        } catch (Exception e) {
            log.error("查询用户信息失败, ID: {}", id, e);
            return ApiResponse.error("查询用户信息失败: " + e.getMessage());
        }
    }
}
