package com.example.user.function.service;

import com.example.common.exception.BizException;
import com.example.common.exception.FunctionVersionConflictException;
import com.example.user.function.dao.FunctionMapper;
import com.example.user.function.dao.FunctionPublishLogMapper;
import com.example.user.function.dto.FunctionPublishRequest;
import com.example.user.function.model.FunctionEntity;
import com.example.user.function.model.FunctionPublishLog;
import com.example.user.function.outbox.service.FunctionEventOutboxService;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FunctionPublishService {

    @Autowired
    private FunctionMapper functionMapper;

    @Autowired
    private FunctionPublishLogMapper logMapper;

    @Autowired
    private FunctionRefreshService refreshService;

    @Autowired
    private FunctionEventOutboxService outboxService;

    @Transactional
    public void publish(FunctionPublishRequest request) throws BizException, FunctionVersionConflictException {

        validateVersion(request);

        FunctionEntity entity = buildEntity(request);

        functionMapper.disableOldVersion(entity.getFunctionCode());
        functionMapper.insert(entity);

        refreshService.refresh(entity.getFunctionCode(), entity.getVersion());

        // ⭐ 写 Outbox（事务一致性）
        outboxService.writeCacheRefreshEvent(
                entity.getFunctionCode(),
                entity.getVersion()
        );

        FunctionPublishLog log = buildFunctionPublishLog(request);
        logMapper.insert(log);
    }

    private static @NonNull FunctionPublishLog buildFunctionPublishLog(FunctionPublishRequest request) {
        FunctionPublishLog log = new FunctionPublishLog();
        log.setFunctionCode(request.getFunctionCode());
        log.setVersion(request.getVersion());
        log.setOperator(request.getOperator());
        log.setPublishType("API");
        // 7. 记录成功日志
        log.setPublishStatus(1);
        return log;
    }

    private void validateVersion(FunctionPublishRequest request) throws FunctionVersionConflictException {
        Integer maxVersion =
                functionMapper.selectMaxVersion(request.getFunctionCode());

        if (maxVersion != null && request.getVersion() <= maxVersion) {
            throw new FunctionVersionConflictException(request.getFunctionCode(), request.getVersion(), maxVersion);
        }
    }

    private FunctionEntity buildEntity(FunctionPublishRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("FunctionPublishRequest is null");
        }

        FunctionEntity entity = new FunctionEntity();
        entity.setFunctionCode(request.getFunctionCode());
        entity.setFunctionName(request.getFunctionName());
        entity.setGroovyScript(request.getGroovyScript());
        entity.setVersion(Long.valueOf(request.getVersion()));
        entity.setStatus(1);

        return entity;
    }

}


/**
 * 七、你现在的系统“安全级别评估”
 * 能力	状态
 * 本地缓存	✅
 * MQ 通知	✅
 * 事务一致性	✅（刚加）
 * 线程隔离	✅
 * 超时控制	✅
 * 防死循环	✅
 * 防拖垮 JVM	✅
 *
 * 👉 已经是“生产可控系统”
 *
 * 八、再往前一步（下一阶段能力）
 *
 * 当你准备继续升级时，我建议的顺序是：
 *
 * Groovy 沙箱（禁止 IO / System / 反射）
 *
 * 函数执行监控（耗时 / 失败率）
 *
 * 函数级限流（按 functionCode）
 *
 * 灰度执行（指定版本）
 */

