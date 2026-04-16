package com.example.user.demo.controller;

import com.example.common.web.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

/**
 * 数据库测试控制器
 * 用于验证数据库连接是否正常
 */
@Slf4j
@RestController
public class DbTestController {

    @Autowired
    private DataSource dataSource;

    /**
     * 测试数据库连接
     * @return 连接测试结果
     */
    @GetMapping("/db/test")
    public ApiResponse<String> test() {
        try {
            // 测试数据库连接
            dataSource.getConnection().close();
            log.info("数据库连接测试成功");
            return ApiResponse.success("数据库连接正常");
        } catch (Exception e) {
            log.error("数据库连接测试失败", e);
            return ApiResponse.error("数据库连接异常: " + e.getMessage());
        }
    }
}
