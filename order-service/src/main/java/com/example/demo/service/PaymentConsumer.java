package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.OrderMapper;

@Service
public class PaymentConsumer {

    @Autowired
    private OrderMapper orderMapper;

    @KafkaListener(topics = "payment_topic", groupId = "order-group")
    public void receivePayment(String orderIdStr) {
        Long orderId = Long.parseLong(orderIdStr);
        System.out.println("收到支付成功消息，准备更新订单状态为已支付: " + orderId);
        
        // 执行真实的 SQL 更新
        orderMapper.payOrder(orderId);
        System.out.println("订单状态更新完毕！");
    }
}