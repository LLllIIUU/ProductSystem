package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.SeckillService;

@RestController
@RequestMapping("/api/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    // 为了方便我们在浏览器里直接敲网址测试，这里暂且用 GetMapping
    @GetMapping("/buy")
    public String buy(@RequestParam Long userId, @RequestParam Long productId) {
        // 直接呼叫秒杀服务
        return seckillService.doSeckill(userId, productId);
    }

    public SeckillService getSeckillService() {
        return seckillService;
    }

}