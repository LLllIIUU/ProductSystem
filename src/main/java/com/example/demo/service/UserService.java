package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;// 注入 UserMapper 以便进行数据库操作

    public String register(User user) {
        if (userMapper.findByUsername(user.getUsername()) != null) {
            return "Username already exists!";
        } else {
            userMapper.insertUser(user);
            return "User registered successfully!";
        }
    }

    public String login(User user) {
        User dbUser = userMapper.findByUsername(user.getUsername());
        if (dbUser == null) {
            return "User not found!";
        }
        if (!dbUser.getPassword().equals(user.getPassword())) {
            return "Incorrect password!";
        }
        return "Login successful!";
    }
}
