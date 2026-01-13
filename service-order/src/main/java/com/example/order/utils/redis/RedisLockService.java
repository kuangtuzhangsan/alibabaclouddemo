package com.example.order.utils.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisLockService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean tryLock(String key, String value, long seconds) {
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, value, Duration.ofSeconds(seconds));
        return Boolean.TRUE.equals(success);
    }

    public void unlock(String key, String value) {
        String val = redisTemplate.opsForValue().get(key);
        if (value.equals(val)) {
            redisTemplate.delete(key);
        }
    }
}
