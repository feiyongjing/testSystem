package com.feiyongjing.living_bill.dao;

import com.feiyongjing.living_bill.enity.Bill;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BillMapper {

    long getTotalBillNumberByUserId(long userId);

    Bill getBillByBillId(long id);

    long getBillCountByMonth(long userId, int year,int month);

    List<Bill> getMonthBill(long userId,int limit, int offset, int year,int month);

    long createBill(Bill bill);
}
