package com.example.order.seckill.controller;

import com.example.common.web.ApiResponse;
import com.example.order.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 秒杀控制器
 */
@Slf4j
@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 秒杀购买
     * @param skuId 商品SKU ID
     * @param userId 用户ID
     * @return 秒杀结果
     */
    @GetMapping("/buy")
    public ApiResponse<String> buy(@RequestParam Long skuId, @RequestParam Long userId) {
        log.info("秒杀请求 - 商品ID: {}, 用户ID: {}", skuId, userId);
        
        if (skuId == null || skuId <= 0) {
            return ApiResponse.error("商品ID不能为空且必须大于0");
        }
        
        if (userId == null || userId <= 0) {
            return ApiResponse.error("用户ID不能为空且必须大于0");
        }
        
        try {
            String result = seckillService.seckill(skuId, userId);
            log.info("秒杀成功 - 商品ID: {}, 用户ID: {}, 结果: {}", skuId, userId, result);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("秒杀失败 - 商品ID: {}, 用户ID: {}", skuId, userId, e);
            return ApiResponse.error("秒杀失败: " + e.getMessage());
        }
    }
}

