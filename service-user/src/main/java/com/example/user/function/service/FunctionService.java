package com.example.user.function.service;

import com.example.user.function.cache.FunctionWrapper;
import com.example.user.function.dao.FunctionMapper;
import com.example.user.function.model.FunctionEntity;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FunctionService {

    @Autowired
    private FunctionMapper functionMapper;

    @Autowired
    private Cache<String, FunctionWrapper> functionCache;

    public FunctionWrapper findActiveByCodeCache(String functionCode) {
        return functionCache.getIfPresent(functionCode);
    }
}
