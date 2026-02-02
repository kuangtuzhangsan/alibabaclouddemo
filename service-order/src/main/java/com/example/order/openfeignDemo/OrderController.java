package com.example.order.openfeignDemo;

import com.example.common.web.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ApiResponse create(@RequestParam Long userId, @RequestParam Long skuId) {
        orderService.createOrder(userId, skuId);
        return ApiResponse.success();
    }
}