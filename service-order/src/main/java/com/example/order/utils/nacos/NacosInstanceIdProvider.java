package com.example.order.utils.nacos;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NacosInstanceIdProvider {

    @Autowired
    private NacosDiscoveryProperties nacosProperties;

    public String getInstanceId() {
        return nacosProperties.getIp() + ":" + nacosProperties.getPort();
    }
}
