package com.example.user.login.controller;

import com.example.user.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redis;

    @PostMapping("/login")
    public Map<String, String> login(@RequestParam String username,
                                     @RequestParam String password) {

        // 演示用：固定用户
        Long userId = 2L;

        String token = jwtUtil.generate(userId);

        // 写入 Redis，供 Gateway 校验
        redis.opsForValue().set("login:" + userId, "1", 24, TimeUnit.HOURS);

        return Collections.singletonMap("token", token);
    }
}

