package com.example.demo.entity;

public class Order {
    private Long id; // 雪花算法生成的订单ID
    private Long userId; // 买家ID
    private Long productId; // 商品ID
    private Integer status; // 订单状态 (1: 成功)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
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
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }


}