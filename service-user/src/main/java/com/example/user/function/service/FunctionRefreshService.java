package com.example.user.function.service;

import com.example.user.function.cache.FunctionWrapper;
import com.example.user.function.compile.GroovyCompiler;
import com.example.user.function.dao.FunctionMapper;
import com.example.user.function.model.FunctionEntity;
import com.github.benmanes.caffeine.cache.Cache;
import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FunctionRefreshService {

    private static final Logger log = LoggerFactory.getLogger(FunctionRefreshService.class);

    @Autowired
    private FunctionMapper functionMapper;

    @Autowired
    private GroovyCompiler groovyCompiler;

    @Autowired
    private Cache<String, FunctionWrapper> functionCache;

    /**
     * 根据 DB 最新状态刷新单个函数缓存
     */
    @Transactional(readOnly = true)
    public void refresh(String functionCode, Long version) {

        FunctionEntity entity = functionMapper.findActiveByCode(functionCode);

        // DB 中已无有效版本 → 清缓存
        if (entity == null) {
            functionCache.invalidate(functionCode);
            return;
        }

        FunctionWrapper cached = functionCache.getIfPresent(functionCode);
        if (cached != null && version <= cached.getVersion()) {
            return;
        }

        try {
            Script script = groovyCompiler.compile(entity.getGroovyScript());

            FunctionWrapper wrapper = new FunctionWrapper(
                    entity.getFunctionCode(),
                    entity.getVersion(),
                    script
            );

            functionCache.put(functionCode, wrapper);
        } catch (Exception e) {
            log.error("Refresh function failed, code={}, version={}", functionCode, version, e);
        }
    }


    /**
     * 全量刷新（预留）
     */
    @Transactional(readOnly = true)
    public void refreshAll() {
        functionCache.invalidateAll();
    }
}

