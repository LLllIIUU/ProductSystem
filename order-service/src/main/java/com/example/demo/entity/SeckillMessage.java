package com.example.demo.entity;

public class SeckillMessage {
    private Long userId; // 买家ID
    private Long productId; // 商品ID

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public SeckillMessage() {
    }

    public SeckillMessage(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }
    
}