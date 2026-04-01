package com.example.demo.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Product; // 确保顶部有这个 import
import com.example.demo.mapper.ProductMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.baomidou.dynamic.datasource.annotation.DS;// 导入动态数据源注解

@Service
public class ProductService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StringRedisTemplate redisTemplate; // 注入 Redis 模板

    private ObjectMapper objectMapper = new ObjectMapper(); // 用于对象和 JSON 之间的转换

    @DS("slave") // 指定这个方法使用 "slave" 数据源
    public Product getProductById(Long id) {
        String cacheKey = "product:" + id;// 定义 Redis 里的钥匙名
        String lockKey = "lock:product:" + id; // 分布式锁的钥匙名
        try {
            // 1. 先查 Redis 缓存
            String productJson = redisTemplate.opsForValue().get(cacheKey);

            if (productJson != null) {
                // 【防御：缓存穿透】如果是我们故意存的空标记，直接返回 null，不让请求打到数据库
                if ("empty".equals(productJson)) {
                    System.out.println("拦截到恶意请求，直接返回空！");
                    return null;
                }
                System.out.println("命中 Redis 缓存！极其快速地返回数据。");
                return objectMapper.readValue(productJson, Product.class);
            }

            // 2. 缓存没有命中，尝试拿分布式锁
            Boolean isLock = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(isLock)) {
                System.out.println("拿到互斥锁！作为代表老老实实去查 MySQL...");
                // 3. 拿到锁的人，去mysql查数据
                try {
                    Product product = productMapper.findById(id);

                    if (product != null) {
                        // 【防御：缓存雪崩】给过期时间加一个随机数（60 + 0~30分钟）
                        long expireTime = 60 + (long) (Math.random() * 30);
                        redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(product), expireTime,
                                TimeUnit.MINUTES);
                    } else {
                        // 【防御：缓存穿透】存入 "empty" 空标记
                        redisTemplate.opsForValue().set(cacheKey, "empty", 5, TimeUnit.MINUTES);
                    }
                    return product;
                } finally {
                    // 【极其重要】查完数据一定要把锁删掉，让别人也能用
                    redisTemplate.delete(lockKey);
                }
            } else {
                // -------- 没拿到锁的人，稍微等一下，然后重新尝试 --------
                System.out.println("没拿到锁，有人正在查数据库，我等 50 毫秒再试...");
                Thread.sleep(50);
                return getProductById(id); // 递归调用自己，重新走一遍流程
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}