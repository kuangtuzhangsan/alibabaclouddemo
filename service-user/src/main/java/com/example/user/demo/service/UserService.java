package com.example.user.demo.service;

import com.example.user.demo.constant.CacheNames;
import com.example.user.demo.entity.User;
import com.example.user.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Cacheable(cacheNames = CacheNames.USER, key = "#id")
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @CachePut(cacheNames = CacheNames.USER, key = "#user.id")
    public User update(User user) {
        userMapper.updateById(user);
        return user;
    }

    @CacheEvict(cacheNames = CacheNames.USER, key = "#id")
    public void delete(Long id) {
        userMapper.deleteById(id);
    }
}