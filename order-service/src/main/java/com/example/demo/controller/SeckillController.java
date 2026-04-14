package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seckill")
public class SeckillController {

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

    @Autowired
    private org.springframework.kafka.core.KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/pay")
    public String pay(@RequestParam Long orderId) {
        // 模拟支付成功，向 Kafka 发送支付成功消息，话题叫 payment_topic
        kafkaTemplate.send("payment_topic", String.valueOf(orderId));
        return "订单 " + orderId + " 支付成功！系统正在后台异步更新状态...";
    }
}