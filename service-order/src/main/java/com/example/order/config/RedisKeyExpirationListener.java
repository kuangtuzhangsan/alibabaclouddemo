package com.example.order.config;

import com.example.order.orderExpireByRedis.listener.OrderExpireListener;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    private final OrderExpireListener orderExpireListener;

    public RedisKeyExpirationListener(
            RedisMessageListenerContainer listenerContainer,
            OrderExpireListener orderExpireListener) {
        super(listenerContainer);
        this.orderExpireListener = orderExpireListener;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        orderExpireListener.handleMessage(expiredKey);
    }
}
