package com.example.user.function.service;

import com.example.user.function.cache.FunctionWrapper;
import com.example.user.function.compile.GroovyCompiler;
import com.example.user.function.dao.FunctionMapper;
import com.example.user.function.model.FunctionEntity;
import com.github.benmanes.caffeine.cache.Cache;
import groovy.lang.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FunctionRefreshService {

    @Autowired
    private FunctionMapper functionMapper;

    @Autowired
    private GroovyCompiler groovyCompiler;

    @Autowired
    private Cache<String, FunctionWrapper> functionCache;

    @Transactional
    public void refresh(String functionCode) {

        FunctionEntity entity = functionMapper.findActiveByCode(functionCode);
        if (entity == null) {
            functionCache.invalidate(functionCode);
            return;
        }

        Script script = groovyCompiler.compile(entity.getGroovyScript());

        FunctionWrapper wrapper = new FunctionWrapper(
                entity.getFunctionCode(),
                entity.getVersion(),
                script
        );

        // 原子替换
        functionCache.put(functionCode, wrapper);
    }
}

