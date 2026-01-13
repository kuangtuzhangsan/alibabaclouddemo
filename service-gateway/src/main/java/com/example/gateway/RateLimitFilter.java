package com.example.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    @Autowired
    private ReactiveStringRedisTemplate redis;

    private static final int MAX_QPS = 10;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String ip = exchange.getRequest().getRemoteAddress().getHostString();
        String key = "gw:rate:" + ip;

        return redis.opsForValue()
                .increment(key)
                .flatMap(count -> {
                    if (count == 1) {
                        redis.expire(key, Duration.ofSeconds(1)).subscribe();
                    }
                    if (count > MAX_QPS) {
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        return exchange.getResponse().setComplete();
                    }
                    return chain.filter(exchange);
                });
    }

    @Override
    public int getOrder() {
        return -90;
    }
}