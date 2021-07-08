package com.feiyongjing.exam.service;

import com.feiyongjing.exam.dao.UserMapper;
import com.feiyongjing.exam.enity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public String getUserPasswordByUsername(String username) {
        return userMapper.getUserPasswordByUsername(username);
    }

    public User getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }

    public void createUser(User user) {
        userMapper.createUser(user.getName(), user.getPassword(), user.getAvatarUrl());
    }
}
