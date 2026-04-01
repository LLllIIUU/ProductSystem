package com.example.demo.service;

import cn.hutool.json.JSONUtil;
import com.example.demo.entity.SeckillMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SeckillService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public String doSeckill(Long userId, Long productId) {

        // 1. 幂等性：防重复下单
        String boughtKey = "seckill:bought:" + userId + ":" + productId;
        // setIfAbsent 就是 Redis 里的 SETNX。如果不存在就设置成功(返回true)，存在就失败(返回false)
        Boolean isFirstTime = redisTemplate.opsForValue().setIfAbsent(boughtKey, "1");
        if (Boolean.FALSE.equals(isFirstTime)) {
            return "您已经抢购过该商品了，请勿重复下单！";
        }

        // 2. 防超卖：Redis 预扣库存
        String stockKey = "seckill:stock:" + productId;
        // decrement 会在 Redis 里原子性地减 1，并返回减完之后的结果
        Long remainStock = redisTemplate.opsForValue().decrement(stockKey);

        System.out.println("当前商品id：" + productId + "剩余库存: " + remainStock);

        if (remainStock < 0) {
            // 库存扣成负数了，说明卖光了。我们要把之前防重复的锁解开（可选，视业务而定），并返回失败
            redisTemplate.delete(boughtKey);
            return "手慢了，商品已售罄！";
        }

        // 3. 削峰填谷：发送消息给 Kafka
        // 能走到这里，说明抢到了！我们写一封信塞进邮筒
        SeckillMessage message = new SeckillMessage(userId, productId);
        kafkaTemplate.send("seckill_topic", JSONUtil.toJsonStr(message));

        return "抢购成功,您的请求已进入队列，正在后台极速处理中";
    }
}