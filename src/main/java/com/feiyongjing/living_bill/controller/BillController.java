package com.feiyongjing.living_bill.controller;

import com.feiyongjing.living_bill.enity.Bill;
import com.feiyongjing.living_bill.enity.PageResponse;
import com.feiyongjing.living_bill.enity.Response;
import com.feiyongjing.living_bill.exception.HttpException;
import com.feiyongjing.living_bill.service.BillService;
import com.feiyongjing.living_bill.service.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v3")
public class BillController {
    private static Pattern DATE_PATTERN = Pattern.compile("^(?:(?!0000)[0-9]{4}([-/.]?)(?:(?:0?[1-9]|1[0-2])\\1(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])\\1(?:29|30)|(?:0?[13578]|1[02])\\1(?:31))|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)([-/.]?)0?2\\2(?:29))$");
    private static Pattern YEAR_MONTH_PATTERN = Pattern.compile("^(?!0000)[0-9]{4}-(0[1-9]|1[0-2])$");
    private BillService billService;

    @Autowired
    public BillController(BillService billService) {
        this.billService = billService;
    }

    @PostMapping("/bill")
    public Response<Bill> createBill(@RequestBody BillMessage billMessage) {
        try {
            return Response.of(200, "账单创建成功", billService.createBill(clean(billMessage)));
        } catch (HttpException e) {
            return Response.of(400, e.getMessage(), null);
        }

    }

    private Bill clean(BillMessage billMessage) {
        if (billMessage.getUserId() == null) {
            throw HttpException.preconditionFailed("非法参数");
        }
        if (billMessage.getUserId() != UserContext.getCurrentUser().getId()) {
            throw HttpException.preconditionFailed("无权添加");
        }
        if (billMessage.getBillAmount() == null || billMessage.getBillAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw HttpException.preconditionFailed("非法参数");
        }
        if (!billMessage.getBillType().equals("收入") && !billMessage.getBillType().equals("支出")) {
            throw HttpException.preconditionFailed("非法参数");
        }
        if (billMessage.getBillCostType() == null || billMessage.getBillCostType().equals("")) {
            throw HttpException.preconditionFailed("非法参数");
        }
        if (!DATE_PATTERN.matcher(billMessage.getBillTime()).matches()) {
            throw HttpException.preconditionFailed("非法参数");
        }
        Bill bill = new Bill();
        bill.setUserId(billMessage.getUserId());
        bill.setBillAmount(billMessage.getBillAmount());
        bill.setBillType(billMessage.getBillType());
        bill.setBillCostType(billMessage.getBillCostType());
        bill.setBillTime(billMessage.getBillTime());
        bill.setRemark(billMessage.getRemark());
        return bill;
    }

    @GetMapping("/month_bill")
    public Response<PageResponse<Bill>> getMonthBill(@RequestParam("userId") Long userId,
                                                     @RequestParam("pageNum") int pageNum,
                                                     @RequestParam("pageSize") int pageSize,
                                                     @RequestParam("yearMonth") String yearMonth) {
        if (userId == null || userId != UserContext.getCurrentUser().getId()) {
            return Response.of(400, "没有权限查询其他用户的账单", null);
        }
        if (!YEAR_MONTH_PATTERN.matcher(yearMonth).matches()) {
            return Response.of(400, "非法参数", null);
        }
        return Response.of(200, "账单查询成功", billService.getMonthBill(userId, pageNum, pageSize, yearMonth));
    }

    public static class BillMessage {
        Long userId;
        BigDecimal billAmount;
        String billType;
        String billCostType;
        String billTime;
        String remark;

        public BillMessage(Long userId, BigDecimal billAmount, String billType, String billCostType, String billTime, String remark) {
            this.userId = userId;
            this.billAmount = billAmount;
            this.billType = billType;
            this.billCostType = billCostType;
            this.billTime = billTime;
            this.remark = remark;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public BigDecimal getBillAmount() {
            return billAmount;
        }

        public void setBillAmount(BigDecimal billAmount) {
            this.billAmount = billAmount;
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

        public String getBillTime() {
            return billTime;
        }

        public void setBillTime(String billTime) {
            this.billTime = billTime;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
