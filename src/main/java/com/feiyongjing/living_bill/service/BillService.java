package com.feiyongjing.living_bill.service;

import com.feiyongjing.living_bill.controller.BillController;
import com.feiyongjing.living_bill.dao.BillMapper;
import com.feiyongjing.living_bill.enity.Bill;
import com.feiyongjing.living_bill.enity.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillService {
    private BillMapper billMapper;

    @Autowired
    public BillService(BillMapper billMapper) {
        this.billMapper = billMapper;
    }

    public Bill createBill(Bill bill) {
        String remark = bill.getRemark();
        if(remark==null){
            bill.setRemark(bill.getBillCostType());
        }
        billMapper.createBill(bill);
        return billMapper.getBillByBillId(bill.getId());
    }

    public PageResponse<Bill> getMonthBill(long userId, int pageNum, int pageSize, String yearMonth) {
        int index = yearMonth.indexOf("-");
        int year = Integer.parseInt(yearMonth.substring(0, index));
        int month = Integer.parseInt(yearMonth.substring(index+1));
        int totalNum = (int) billMapper.getBillCountByMonth(userId,year,month);
        int totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        int limit=pageSize;
        int offset=(pageNum-1)*pageSize;
        List<Bill> billList=billMapper.getMonthBill(userId,limit,offset,year,month);

        return new PageResponse<>(pageNum, pageSize, totalPage, billList);
    }

    public long getTotalBillNumberByUserId(long userId) {
        return billMapper.getTotalBillNumberByUserId(userId);
    }
}
