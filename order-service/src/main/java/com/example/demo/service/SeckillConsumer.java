package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.demo.entity.Order;
import com.example.demo.entity.SeckillMessage;
import com.example.demo.mapper.OrderMapper;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;

@Component
public class SeckillConsumer {
    @Autowired
    private org.springframework.kafka.core.KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private OrderMapper orderMapper;

    // tell Spring to listen to the "seckill_topic" topic in Kafka, and use this
    // method to process incoming messages
    @KafkaListener(topics = "seckill_topic", groupId = "seckill-group")
    public void receiveMessage(String messageJson) {
        System.out.println("消费者从 Kafka 收到一封信: " + messageJson);

        // 1. 把 JSON 字符串变回我们之前写的信件对象
        SeckillMessage message = JSONUtil.toBean(messageJson, SeckillMessage.class);

        // 2. 组装最终的订单实体
        Order order = new Order();
        // 雪花算法生成全局唯一 ID，保证订单号不重复
        order.setId(IdUtil.getSnowflakeNextId());
        order.setUserId(message.getUserId());
        order.setProductId(message.getProductId());
        order.setStatus(1); // 1 表示成功

        // 3. 存入 MySQL 主库
        try {
            orderMapper.insertOrder(order);
            kafkaTemplate.send("deduct_stock_topic", JSONUtil.toJsonStr(message));
            System.out.println("订单落盘成功！已发送指令让库存服务扣减真实库存~");
            System.out.println("订单创建成功！雪花算法订单号：" + order.getId());
        } catch (Exception e) {
            // 如果触发了SQL 里的 UNIQUE KEY (一个人买了两单)，这里就会报错拦截
            System.out.println("订单创建失败 (可能是重复下单): " + e.getMessage());
        }
    }
}