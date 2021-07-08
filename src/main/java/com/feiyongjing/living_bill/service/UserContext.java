package com.feiyongjing.living_bill.service;

import com.feiyongjing.living_bill.enity.User;

public class UserContext {
    public static ThreadLocal<User> currentUser= new ThreadLocal<>();
    public static User getCurrentUser(){
        return currentUser.get();
    }
    public static void setCurrentUser(User user){
        currentUser.set(user);
    }
}
