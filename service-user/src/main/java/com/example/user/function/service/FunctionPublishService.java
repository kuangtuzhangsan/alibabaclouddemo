package com.example.user.function.service;

import com.example.user.function.dao.FunctionMapper;
import com.example.user.function.dao.FunctionPublishLogMapper;
import com.example.user.function.dto.FunctionPublishRequest;
import com.example.user.function.event.FunctionCacheRefreshEvent;
import com.example.user.function.model.FunctionEntity;
import com.example.user.function.model.FunctionPublishLog;
import com.example.user.function.mq.FunctionCacheEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class FunctionPublishService {

    @Autowired
    private FunctionMapper functionMapper;

    @Autowired
    private FunctionPublishLogMapper logMapper;

    @Autowired
    private FunctionRefreshService refreshService;

    @Autowired
    private FunctionCacheEventProducer eventProducer;

    @Transactional
    public void publish(FunctionPublishRequest request) {

        FunctionPublishLog log = new FunctionPublishLog();
        log.setFunctionCode(request.getFunctionCode());
        log.setVersion(request.getVersion());
        log.setOperator(request.getOperator());
        log.setPublishType("API");

        try {
            // 1. 版本冲突校验
            validateVersion(request);

            // 2. 构造实体
            FunctionEntity entity = new FunctionEntity();
            entity.setFunctionCode(request.getFunctionCode());
            entity.setFunctionName(request.getFunctionName());
            entity.setGroovyScript(request.getGroovyScript());
            entity.setVersion(Long.valueOf(request.getVersion()));
            entity.setStatus(1);

            // 3. 失效旧版本
            functionMapper.disableOldVersion(entity.getFunctionCode());

            // 4. 插入新版本
            functionMapper.insert(entity);

            // 5. 本机刷新
            refreshService.refresh(entity.getFunctionCode(), entity.getVersion());

            // 6. 事务提交后发 MQ
            registerAfterCommit(entity);

            // 7. 记录成功日志
            log.setPublishStatus(1);
            logMapper.insert(log);

        } catch (Exception e) {

            // 记录失败日志
            log.setPublishStatus(0);
            log.setFailReason(e.getMessage());
            logMapper.insert(log);

            throw e;
        }
    }

    private void validateVersion(FunctionPublishRequest request) {
        Integer maxVersion =
                functionMapper.selectMaxVersion(request.getFunctionCode());

        if (maxVersion != null && request.getVersion() <= maxVersion) {
            throw new IllegalStateException(
                    "Version conflict, max version = " + maxVersion
            );
        }
    }

    private void registerAfterCommit(FunctionEntity entity) {

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        FunctionCacheRefreshEvent event =
                                new FunctionCacheRefreshEvent();
                        event.setFunctionCode(entity.getFunctionCode());
                        event.setVersion(entity.getVersion());
                        event.setFullRefresh(false);
                        eventProducer.send(event);
                    }
                }
        );
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

