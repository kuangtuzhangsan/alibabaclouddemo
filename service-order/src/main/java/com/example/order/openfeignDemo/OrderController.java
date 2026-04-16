package com.example.order.openfeignDemo;

import com.example.common.web.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     * @param userId 用户ID
     * @param skuId 商品SKU ID
     * @return 创建结果
     */
    @PostMapping("/create")
    public ApiResponse<Void> createOrder(@RequestParam Long userId, @RequestParam Long skuId) {
        log.info("创建订单请求 - 用户ID: {}, 商品SKU ID: {}", userId, skuId);
        
        if (userId == null || userId <= 0) {
            return ApiResponse.error("用户ID不能为空且必须大于0");
        }
        
        if (skuId == null || skuId <= 0) {
            return ApiResponse.error("商品SKU ID不能为空且必须大于0");
        }
        
        try {
            orderService.createOrder(userId, skuId);
            log.info("订单创建成功 - 用户ID: {}, 商品SKU ID: {}", userId, skuId);
            return ApiResponse.success("订单创建成功");
        } catch (Exception e) {
            log.error("订单创建失败 - 用户ID: {}, 商品SKU ID: {}", userId, skuId, e);
            return ApiResponse.error("订单创建失败: " + e.getMessage());
        }
    }
}