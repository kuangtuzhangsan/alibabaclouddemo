package com.example.order.seckill.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.common.util.JsonUtils;
import com.example.order.seckill.entity.MqOutbox;
import com.example.order.seckill.entity.SeckillEvent;
import com.example.order.seckill.mapper.MqOutboxMapper;
import com.example.order.seckill.mq.SeckillConsumer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
public class SeckillService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MqOutboxMapper outboxMapper;

    private DefaultRedisScript<Long> script;

    @PostConstruct
    public void init() throws Exception {
        script = new DefaultRedisScript<>();
        script.setResultType(Long.class);

        Resource res = new ClassPathResource("lua/seckill.lua");
        String text = StreamUtils.copyToString(res.getInputStream(), StandardCharsets.UTF_8);
        script.setScriptText(text);
    }

    @SentinelResource(value="seckill", blockHandler="block")
    @Transactional
    public String seckill(Long skuId, Long userId) {

        Long r = redisTemplate.execute(
                script,
                Arrays.asList("seckill:stock:"+skuId, "seckill:user:"+skuId+":"+userId)
        );

        if (r == -1) return "SOLD_OUT";
        if (r == -2) return "REPEAT";

        SeckillEvent event = new SeckillEvent(skuId, userId);

        MqOutbox out = new MqOutbox();
        out.setTopic(SeckillConsumer.SECKILL_ORDER_TOPIC);
        out.setMsgKey(skuId+"_"+userId);
        out.setPayload(JsonUtils.toJson(event));
        outboxMapper.insert(out);

        return "SUCCESS";
    }

    public String block(Long skuId, Long userId, BlockException e){
        return "RATE_LIMIT";
    }
}






