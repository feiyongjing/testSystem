package com.feiyongjing.living_bill.enity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class User {
    long id;
    String name;
    @JsonIgnore
    String password;
    String avatarUrl;
    Date createdAt;
    Date updatedAt;

    public User() {
    }

    public User(String name, String password, String avatarUrl) {
        this.name = name;
        this.password = password;
        this.avatarUrl = avatarUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
