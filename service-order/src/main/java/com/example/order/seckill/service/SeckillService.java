package com.example.order.seckill.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.common.util.JsonUtils;
import com.example.order.seckill.entity.MqOutbox;
import com.example.order.seckill.entity.SeckillEvent;
import com.example.order.seckill.mapper.MqOutboxMapper;
import com.example.order.seckill.mq.SeckillConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

/**
 * 秒杀服务
 * 使用 Redis Lua 脚本保证原子性，Outbox 模式保证消息可靠性
 */
@Service
public class SeckillService {

    private static final Logger log = LoggerFactory.getLogger(SeckillService.class);

    private static final String STOCK_KEY_PREFIX = "seckill:stock:";
    private static final String USER_KEY_PREFIX = "seckill:user:";
    private static final Long SOLD_OUT = -1L;
    private static final Long REPEAT = -2L;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MqOutboxMapper outboxMapper;

    private DefaultRedisScript<Long> seckillScript;

    @PostConstruct
    public void init() throws IOException {
        seckillScript = new DefaultRedisScript<>();
        seckillScript.setResultType(Long.class);
        seckillScript.setLocation(new ClassPathResource("lua/seckill.lua"));
    }

    /**
     * 秒杀接口
     * @return SUCCESS / SOLD_OUT / REPEAT / RATE_LIMIT
     */
    @SentinelResource(value = "seckill", blockHandler = "handleBlock")
    @Transactional
    public String seckill(Long skuId, Long userId) {
        Long result = executeSeckillLua(skuId, userId);

        if (SOLD_OUT.equals(result)) {
            log.info("秒杀失败-库存不足: skuId={}, userId={}", skuId, userId);
            return "SOLD_OUT";
        }
        
        if (REPEAT.equals(result)) {
            log.info("秒杀失败-重复购买: skuId={}, userId={}", skuId, userId);
            return "REPEAT";
        }

        saveOutboxEvent(skuId, userId);
        log.info("秒杀成功: skuId={}, userId={}", skuId, userId);
        
        return "SUCCESS";
    }

    /**
     * 执行秒杀 Lua 脚本
     */
    private Long executeSeckillLua(Long skuId, Long userId) {
        String stockKey = STOCK_KEY_PREFIX + skuId;
        String userKey = USER_KEY_PREFIX + skuId + ":" + userId;
        
        return redisTemplate.execute(
                seckillScript,
                Arrays.asList(stockKey, userKey)
        );
    }

    /**
     * 保存事件到 Outbox
     */
    private void saveOutboxEvent(Long skuId, Long userId) {
        SeckillEvent event = new SeckillEvent(skuId, userId);

        MqOutbox outbox = new MqOutbox();
        outbox.setTopic(SeckillConsumer.SECKILL_ORDER_TOPIC);
        outbox.setMsgKey(skuId + "_" + userId);
        outbox.setPayload(JsonUtils.toJson(event));
        
        outboxMapper.insert(outbox);
    }

    /**
     * Sentinel 限流处理
     */
    public String handleBlock(Long skuId, Long userId, BlockException e) {
        log.warn("秒杀被限流: skuId={}, userId={}", skuId, userId);
        return "RATE_LIMIT";
    }
}
