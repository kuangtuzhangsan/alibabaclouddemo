package com.example.common.util;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Nacos 实例ID提供者（公共模块）
 * 统一获取当前服务实例标识
 */
@Component
public class NacosInstanceIdProvider {

    @Autowired
    private NacosDiscoveryProperties nacosProperties;

    /**
     * 获取实例ID（格式：ip:port）
     */
    public String getInstanceId() {
        return nacosProperties.getIp() + ":" + nacosProperties.getPort();
    }

    /**
     * 获取实例IP
     */
    public String getIp() {
        return nacosProperties.getIp();
    }

    /**
     * 获取实例端口
     */
    public int getPort() {
        return nacosProperties.getPort();
    }
}
