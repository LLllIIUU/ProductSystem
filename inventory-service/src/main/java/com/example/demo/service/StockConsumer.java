package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.demo.entity.SeckillMessage;
import com.example.demo.mapper.ProductMapper;

import cn.hutool.json.JSONUtil; // 如果你用的不是hutool，请换成你自己的JSON工具包

@Service
public class StockConsumer {

    @Autowired
    private ProductMapper productMapper;

    // 监听刚才订单服务发消息的那个新话题
    @KafkaListener(topics = "deduct_stock_topic", groupId = "inventory-group")
    public void receive(String messageStr) {
        System.out.println("库存服务收到指令: 准备扣减真实库存 -> " + messageStr);
        
        // 1. 拆开信封
        SeckillMessage message = JSONUtil.toBean(messageStr, SeckillMessage.class);
        
        // 2. 执行真正的 SQL 扣减
        int result = productMapper.deductStock(message.getProductId());
        
        if (result > 0) {
            System.out.println("数据库真实库存扣减成功！商品ID: " + message.getProductId());
        } else {
            System.out.println("数据库真实库存扣减失败（可能已无库存）！商品ID: " + message.getProductId());
        }
    }
}