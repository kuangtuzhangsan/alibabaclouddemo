package com.example.order.config;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.example.order.utils.SnowflakeGenerator;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.order.**.mapper")
public class MybatisPlusConfig {

    @Bean
    public GlobalConfig globalConfig(SnowflakeGenerator snowflakeGenerator) {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setIdentifierGenerator(snowflakeGenerator);
        return globalConfig;
    }
}
