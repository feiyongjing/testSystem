package com.feiyongjing.living_bill.utils;

import com.feiyongjing.living_bill.enity.CostType;

import java.util.LinkedList;
import java.util.List;

public class InitialCostType {
    public static List<CostType> initialCostTypeModel = new LinkedList<>();
    public static final String[] incomeTypeArray = new String[]{"工资", "兼职", "理财", "礼金"};
    public static final String[] spendingTypeArray = new String[]{
            "餐饮", "购物", "日用", "交通", "蔬菜", "水果", "零食", "运动",
            "娱乐", "通讯", "服饰", "美容", "住房", "居家", "孩子", "长辈",
            "社交", "旅行", "烟酒", "数码", "汽车", "医疗", "书籍", "学习",
            "宠物", "礼金", "亲友", "礼物", "彩票", "捐赠", "快递", "办公", "维修"};

    static {
        for (String incomeType : incomeTypeArray) {
            initialCostTypeModel.add(new CostType(null,"收入", incomeType, "展示", "默认"));
        }
        for (String spendingType : spendingTypeArray) {
            initialCostTypeModel.add(new CostType(null,"支出", spendingType, "展示", "默认"));
        }
    }
}
