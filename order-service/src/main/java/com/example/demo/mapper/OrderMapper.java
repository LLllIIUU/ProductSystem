package com.example.demo.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.demo.entity.Order;

@Mapper
public interface OrderMapper {
    @Insert("INSERT INTO orders (id, user_id, product_id, status) VALUES (#{id}, #{userId}, #{productId}, #{status})")
    void insertOrder(Order order);

    @Select("SELECT * FROM orders WHERE id = #{orderId}")
    Order selectById(Long orderId);

    @Select("SELECT * FROM orders WHERE user_id = #{userId}")
    java.util.List<Order> selectByUserId(Long userId);

    @org.apache.ibatis.annotations.Update("UPDATE orders SET status = 1 WHERE id = #{orderId}")
    int payOrder(Long orderId);
}