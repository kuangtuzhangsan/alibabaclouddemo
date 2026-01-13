package com.example.gateway.config;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executor;

@Configuration
public class NacosGatewayRouteConfig {

    private static final String DATA_ID = "gateway-routes.json";
    private static final String GROUP = "GATEWAY_GROUP";

    @Autowired
    private NacosConfigManager nacosConfigManager;

    @Autowired
    private RouteDefinitionWriter routeWriter;

    @Autowired
    private ApplicationEventPublisher publisher;

    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() throws Exception {
        // 启动时加载一次
        String config = nacosConfigManager.getConfigService()
                .getConfig(DATA_ID, GROUP, 5000);
        refreshRoutes(config);

        // 注册监听
        nacosConfigManager.getConfigService()
                .addListener(DATA_ID, GROUP, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        refreshRoutes(configInfo);
                    }
                });
    }

    private void refreshRoutes(String config) {
        try {
            List<RouteDefinition> routes = mapper.readValue(
                    config, new TypeReference<List<RouteDefinition>>() {}
            );

            routes.forEach(r -> routeWriter.save(Mono.just(r)).subscribe());
            publisher.publishEvent(new RefreshRoutesEvent(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
