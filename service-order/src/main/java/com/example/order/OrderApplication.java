package com.example.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.*;

@SpringBootApplication
@MapperScan("com.example.order.**.mapper")
@EnableFeignClients(basePackages = "com.example.order")
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

    @Bean("couponExecutor")
    public ExecutorService couponExecutor() {
        ThreadPoolExecutor e = new ThreadPoolExecutor(
                8,
                16,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(2000),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        return e;
    }
}