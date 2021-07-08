package com.feiyongjing.exam.dao;

import com.feiyongjing.exam.enity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void createUser(String name, String password, String avatarUrl);

    String getUserPasswordByUsername(String name);

    User getUserByUsername(String username);

}
