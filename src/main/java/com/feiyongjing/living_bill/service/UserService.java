package com.feiyongjing.living_bill.service;

import com.feiyongjing.living_bill.dao.UserAttachmentTableMapper;
import com.feiyongjing.living_bill.dao.UserMapper;
import com.feiyongjing.living_bill.enity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class UserService {
    private UserMapper userMapper;
    private UserAttachmentTableMapper userAttachmentTableMapper;

    @Autowired
    public UserService(UserMapper userMapper, UserAttachmentTableMapper userAttachmentTableMapper) {
        this.userMapper = userMapper;
        this.userAttachmentTableMapper = userAttachmentTableMapper;
    }

    public String getUserPasswordByUsername(String username) {
        return userMapper.getUserPasswordByUsername(username);
    }

    public User getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }

    public int getUserFirstCreatedBillTimeByUserId(long userId) {
        int daysNum=0;
        Date userFirstCreatedBillTime = userAttachmentTableMapper.getUserFirstCreatedBillTimeByUserId(userId);
        if(userFirstCreatedBillTime==null) return daysNum;
        Date date = new Date();
        long time = date.getTime()-userFirstCreatedBillTime.getTime();
        daysNum = (int) (time/(1000*3600*24))+1;
        return daysNum;
    }
}
