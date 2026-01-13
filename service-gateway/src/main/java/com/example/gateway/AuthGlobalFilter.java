package com.example.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ReactiveStringRedisTemplate redis;

    private static final Set<String> WHITE = new HashSet<>(Arrays.asList(
            "/user/login",
            "/user/register"
    ));

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        if (WHITE.contains(path)) {
            return chain.filter(exchange);
        }

        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token == null) {
            return unauthorized(exchange);
        }

        return Mono.fromCallable(() -> jwtUtil.getUserId(token))
                .flatMap(userId ->
                        redis.hasKey("login:" + userId)
                                .flatMap(ok -> {
                                    if (!ok) return unauthorized(exchange);

                                    ServerHttpRequest req = exchange.getRequest().mutate()
                                            .header("X-User-Id", userId.toString())
                                            .build();

                                    return chain.filter(exchange.mutate().request(req).build());
                                })
                )
                .onErrorResume(e -> unauthorized(exchange));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -100;
    }
}

