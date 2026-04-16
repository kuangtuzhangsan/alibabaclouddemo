package com.example.user.login.controller;

import com.example.common.util.JwtUtil;
import com.example.common.web.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redis;

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果（包含token）
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, String>> login(@RequestParam String username,
                                                  @RequestParam String password) {
        log.info("用户登录请求 - 用户名: {}", username);
        
        if (username == null || username.trim().isEmpty()) {
            return ApiResponse.error("用户名不能为空");
        }
        
        if (password == null || password.trim().isEmpty()) {
            return ApiResponse.error("密码不能为空");
        }
        
        try {
            // 演示用：固定用户（实际应该查询数据库验证）
            Long userId = 2L;
            
            // 生成JWT Token
            String token = jwtUtil.generate(userId);
            log.debug("生成JWT Token - 用户ID: {}, Token: {}", userId, token);

            // 写入 Redis，供 Gateway 校验
            String redisKey = "login:" + userId;
            redis.opsForValue().set(redisKey, "1", 24, TimeUnit.HOURS);
            log.debug("Redis缓存登录状态 - Key: {}, TTL: 24小时", redisKey);

            // 构建响应数据
            Map<String, String> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", userId.toString());
            data.put("username", username);
            
            log.info("用户登录成功 - 用户名: {}, 用户ID: {}", username, userId);
            return ApiResponse.success("登录成功", data);
            
        } catch (Exception e) {
            log.error("用户登录失败 - 用户名: {}", username, e);
            return ApiResponse.error("登录失败: " + e.getMessage());
        }
    }
}
