package com.example.demo.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Product; // 确保顶部有这个 import
import com.example.demo.mapper.ProductMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProductService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StringRedisTemplate redisTemplate; // 注入 Redis 模板

    private ObjectMapper objectMapper = new ObjectMapper(); // 用于对象和 JSON 之间的转换

    public Product getProductById(Long id) {
        // 定义 Redis 里的钥匙名，比如 "product:1"
        String cacheKey = "product:" + id;

        try {
            // ================= 1. 先查 Redis 缓存 =================
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

            // ================= 2. Redis 没有，去查 MySQL =================
            System.out.println("Redis 没数据，老老实实去查 MySQL...");
            Product product = productMapper.findById(id);

            // ================= 3. 查到结果后，存入 Redis =================
            if (product != null) {
                // 【防御：缓存雪崩】给过期时间加一个随机数（比如 60分钟 + 随机0~30分钟）
                // 这样可以防止大量商品在同一秒钟同时过期，导致 MySQL 瞬间被压垮
                long expireTime = 60 + (long) (Math.random() * 30);
                redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(product), expireTime,
                        TimeUnit.MINUTES);
            } else {
                // 【防御：缓存穿透】MySQL 里也没有这个商品（比如黑客故意查 ID=-1）
                // 我们在 Redis 里存一个 "empty" 标记，只存活 5 分钟
                redisTemplate.opsForValue().set(cacheKey, "empty", 5, TimeUnit.MINUTES);
            }

            return product;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}