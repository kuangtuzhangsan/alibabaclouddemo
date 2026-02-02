package com.example.order.orderExpireByRedis.controller;

import com.example.common.web.ApiResponse;
import com.example.order.orderExpireByRedis.service.TOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/order/cancel/subscription")
public class TOrderController {

    @Autowired
    private TOrderService orderService;

    /**
     * 下单超时自动取消
     * @param id
     * @param amount
     * @param orderNo
     * @return
     */
    @GetMapping("/{id}")
    public ApiResponse<String> get(@PathVariable Long id, @RequestParam BigDecimal amount, @RequestParam String orderNo) {

        return ApiResponse.success(orderService.createOrder(id, amount, orderNo));
    }
}
