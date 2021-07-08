package com.feiyongjing.living_bill.enity;

public class CostType {
    Long id;
    Long userId;
    String billType;
    String billCostType;
    String statusDisplay;
    String source;

    public CostType() {
    }

    public CostType(Long userId, String billType, String billCostType, String statusDisplay, String source) {
        this.userId = userId;
        this.billType = billType;
        this.billCostType = billCostType;
        this.statusDisplay = statusDisplay;
        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getBillCostType() {
        return billCostType;
    }

    public void setBillCostType(String billCostType) {
        this.billCostType = billCostType;
    }

    public String getStatusDisplay() {
        return statusDisplay;
    }

    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
