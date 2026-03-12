package com.example.demo.mapper;

import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

@Mapper// 这是一个 MyBatis 的 Mapper 接口，用于定义数据库操作方法
public interface UserMapper {
    @Insert("INSERT INTO t_user(username, password, phone) VALUES(#{username}, #{password}, #{phone})")
    int insertUser(User user);
    @Select("SELECT * FROM t_user WHERE username = #{username}")
    User findByUsername(String username);
}