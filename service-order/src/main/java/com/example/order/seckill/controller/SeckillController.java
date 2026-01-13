package com.example.order.seckill.controller;


import com.example.order.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @GetMapping("/buy")
    public String buy(@RequestParam Long skuId, @RequestParam Long userId) {
        return seckillService.seckill(skuId, userId);
    }
}

