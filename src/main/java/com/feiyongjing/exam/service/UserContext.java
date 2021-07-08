package com.feiyongjing.exam.service;

import com.feiyongjing.exam.enity.User;

public class UserContext {
    private static ThreadLocal<User> currentUser= new ThreadLocal<>();
    public static User getCurrentUser(){
        return currentUser.get();
    }
    public static void setCurrentUser(User user){
        currentUser.set(user);
    }

    public static void clearCurrentUser() {
        currentUser.remove();
    }
}
