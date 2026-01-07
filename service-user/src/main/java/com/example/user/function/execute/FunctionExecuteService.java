package com.example.user.function.execute;

import com.example.user.function.cache.FunctionWrapper;
import com.example.user.function.service.FunctionRefreshService;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FunctionExecuteService {

    @Autowired
    private Cache<String, FunctionWrapper> functionCache;

    @Autowired
    private FunctionRefreshService refreshService;

    public Object execute(String functionCode, Map<String, Object> params) {

        FunctionWrapper wrapper = functionCache.getIfPresent(functionCode);

        if (wrapper == null) {
            refreshService.refresh(functionCode);
            wrapper = functionCache.getIfPresent(functionCode);
        }

        if (wrapper == null) {
            throw new IllegalStateException("Function not found: " + functionCode);
        }

        return wrapper.execute(params);
    }
}

