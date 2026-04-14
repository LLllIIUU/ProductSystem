package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.demo.entity.Product;

@Mapper
public interface ProductMapper {
    @Select("SELECT * FROM t_product WHERE id = #{id}")
    Product findById(Long id);
    @org.apache.ibatis.annotations.Update("UPDATE product SET stock = stock - 1 WHERE id = #{productId} AND stock > 0")
    int deductStock(Long productId);
}
