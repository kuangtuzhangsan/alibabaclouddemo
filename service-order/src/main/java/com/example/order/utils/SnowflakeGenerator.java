package com.example.order.utils;


import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.stereotype.Component;


@Component
public class SnowflakeGenerator implements IdentifierGenerator {

    private final DefaultIdentifierGenerator generator = new DefaultIdentifierGenerator();

    @Override
    public Number nextId(Object entity) {
        return generator.nextId(entity);
    }

    @Override
    public String nextUUID(Object entity) {
        return generator.nextUUID(entity);
    }
}
