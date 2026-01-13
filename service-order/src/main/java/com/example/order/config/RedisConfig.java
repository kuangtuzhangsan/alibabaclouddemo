package com.example.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        StringRedisSerializer key = new StringRedisSerializer();
        // 所有 Redis 操作统一用 JSON
        GenericJackson2JsonRedisSerializer value = new GenericJackson2JsonRedisSerializer();

        template.setKeySerializer(key);
        template.setHashKeySerializer(key);
        template.setValueSerializer(value);
        template.setHashValueSerializer(value);

        template.afterPropertiesSet();
        return template;
    }
}


