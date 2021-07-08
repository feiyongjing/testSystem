package com.feiyongjing.living_bill.service;

import com.feiyongjing.living_bill.dao.CostTypeMapper;
import com.feiyongjing.living_bill.dao.UserMapper;
import com.feiyongjing.living_bill.enity.CostType;
import com.feiyongjing.living_bill.enity.User;
import com.feiyongjing.living_bill.utils.InitialCostType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private UserMapper userMapper;
    private CostTypeMapper costTypeMapper;
    private UserService userService;


    @Autowired
    public AuthService(UserMapper userMapper, CostTypeMapper costTypeMapper, UserService userService) {
        this.userMapper = userMapper;
        this.costTypeMapper = costTypeMapper;
        this.userService = userService;
    }
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    public void createUser(User user){
        userMapper.createUser(user.getName(),user.getPassword(),user.getAvatarUrl());
        long userId = userService.getUserByUsername(user.getName()).getId();
        for (CostType costType : InitialCostType.initialCostTypeModel) {
            costType.setUserId(userId);
            costTypeMapper.insertCostType(costType);
            costType.setUserId(null);
        }
    }
}
