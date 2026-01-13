package com.example.order.seckill.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
public class SeckillService {

    private static final String LUA_PATH = "lua/seckill.lua";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    private DefaultRedisScript<Long> seckillScript;

    @PostConstruct
    public void init() throws Exception {
        seckillScript = new DefaultRedisScript<>();
        seckillScript.setResultType(Long.class);

        Resource res = new ClassPathResource("lua/seckill.lua");
        String scriptText = StreamUtils.copyToString(res.getInputStream(), StandardCharsets.UTF_8);
        seckillScript.setScriptText(scriptText);
    }

    /**
     * 返回 "SUCCESS","SOLD_OUT","REPEAT"
     */
    public String seckill(Long skuId, Long userId) {
        String stockKey = "seckill:stock:" + skuId;
        String userKey = "seckill:user:" + skuId + ":" + userId;

        Long result = redisTemplate.execute(
                seckillScript,
                Arrays.asList(stockKey, userKey)
        );

        if (result == null) {
            return "ERROR";
        }
        if (result == -1L) return "SOLD_OUT";
        if (result == -2L) return "REPEAT";

        // 发送下单消息到 MQ（削峰）
        String payload = skuId + "," + userId;
        rocketMQTemplate.convertAndSend("SECKILL_ORDER_TOPIC", payload);
        return "SUCCESS";
    }
}





