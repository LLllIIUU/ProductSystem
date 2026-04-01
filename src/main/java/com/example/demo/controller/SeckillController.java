package com.example.demo.controller;

import com.example.demo.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    // 注入 OrderMapper 用来查询数据库
    @Autowired
    private com.example.demo.mapper.OrderMapper orderMapper;

    // 按订单ID查询订单，测试网址：http://localhost:8080/api/seckill/order?orderId=2039227070545100800
    @GetMapping("/order")
    public com.example.demo.entity.Order getOrderById(@RequestParam Long orderId) {
         return orderMapper.selectById(orderId);
    }

    // 按用户ID查询订单，测试网址：http://localhost:8080/api/seckill/order/user?userId=5515
    @GetMapping("/order/user")
    public java.util.List<com.example.demo.entity.Order> getOrdersByUserId(@RequestParam Long userId) {

        return orderMapper.selectByUserId(userId);
    }
}