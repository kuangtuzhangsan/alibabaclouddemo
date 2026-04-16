package com.example.order.demo.service;

import com.example.common.entity.User;
import com.example.order.demo.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户信息，如果不存在返回null
     */
    public User getById(Long id) {
        log.debug("查询用户, ID: {}", id);
        
        if (id == null || id <= 0) {
            log.warn("无效的用户ID: {}", id);
            return null;
        }
        
        try {
            User user = userMapper.selectById(id);
            if (user == null) {
                log.info("用户不存在, ID: {}", id);
            } else {
                log.debug("查询到用户: {}", user);
            }
            return user;
        } catch (Exception e) {
            log.error("查询用户失败, ID: {}", id, e);
            throw new RuntimeException("查询用户失败", e);
        }
    }
}
