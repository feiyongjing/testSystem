package com.feiyongjing.living_bill.enity;

public class LoginResponse {
    long billTotalNumber;
    int totalNumberOfDays;
    User user;

    public LoginResponse() {
    }

    public LoginResponse(long billTotalNumber, int totalNumberOfDays, User user) {
        this.billTotalNumber = billTotalNumber;
        this.totalNumberOfDays = totalNumberOfDays;
        this.user = user;
    }

    public long getBillTotalNumber() {
        return billTotalNumber;
    }

    public void setBillTotalNumber(long billTotalNumber) {
        this.billTotalNumber = billTotalNumber;
    }

    public int getTotalNumberOfDays() {
        return totalNumberOfDays;
    }

    public void setTotalNumberOfDays(int totalNumberOfDays) {
        this.totalNumberOfDays = totalNumberOfDays;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
