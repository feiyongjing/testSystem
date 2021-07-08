package com.feiyongjing.living_bill.controller;

import com.feiyongjing.living_bill.enity.CostType;
import com.feiyongjing.living_bill.enity.Response;
import com.feiyongjing.living_bill.exception.HttpException;
import com.feiyongjing.living_bill.service.CostTypeService;
import com.feiyongjing.living_bill.service.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v2")
public class CostTypeController {
    private CostTypeService costTypeService;

    @Autowired
    public CostTypeController(CostTypeService costTypeService) {
        this.costTypeService = costTypeService;
    }

    @GetMapping("/bill_type")
    public Response<List<CostType>> getShownCostTypeByBillType(@RequestParam("billType") String billType) {
        long userId = UserContext.getCurrentUser().getId();
        return Response.of(OK.value(), "获取成功",
                costTypeService.getShownCostTypeByBillType(userId, billType, "展示"));
    }

    @GetMapping("/{bill_type}")
    public Response<List<CostType>> getAllCostTypeByBillType(@PathVariable("bill_type") String billType) {
        long userId = UserContext.getCurrentUser().getId();
        return Response.of(OK.value(), "获取成功",
                costTypeService.getAllCostTypeByBillType(userId, billType));
    }

    @PostMapping("/bill_type")
    public Response<CostType> createBillCostType(@RequestBody CostType costType) {
        try {
            clean(costType);
            return Response.of(OK.value(), "添加成功",
                    costTypeService.createBillCostType(costType));
        } catch (HttpException e) {
            return Response.of(BAD_REQUEST.value(), e.getMessage(),
                    null);
        }
    }

    @PostMapping("/{id}")
    public Response<CostType> updateBillCostType(@PathVariable("id") long id, @RequestBody StatusDisplayAndSource statusDisplayAndSource) {
        try {
            clean(statusDisplayAndSource);
            return Response.of(OK.value(), "更新成功",
                    costTypeService.updateBillCostType(id, statusDisplayAndSource));
        } catch (HttpException e) {
            return Response.of(BAD_REQUEST.value(), e.getMessage(),
                    null);
        }
    }

    private void clean(StatusDisplayAndSource statusDisplayAndSource) {
        if (!statusDisplayAndSource.getStatusDisplay().equals("展示") && !statusDisplayAndSource.getStatusDisplay().equals("不展示")) {
            throw HttpException.preconditionFailed("非法参数");
        }
        if (!statusDisplayAndSource.getSource().equals("默认") && !statusDisplayAndSource.getSource().equals("自定义")) {
            throw HttpException.preconditionFailed("非法参数");
        }
    }


    private void clean(CostType costType) {
        costType.setId(null);
        if (costType.getUserId() != UserContext.getCurrentUser().getId()) {
            throw HttpException.preconditionFailed("无权修改别人的账单费用类型");
        }
        if (costType.getBillCostType() == null || costType.getBillCostType().equals("")) {
            throw HttpException.preconditionFailed("非法参数");
        }
        if (!costType.getBillType().equals("收入") && !costType.getBillType().equals("支出")) {
            throw HttpException.preconditionFailed("非法参数");
        }
        costType.setStatusDisplay("展示");
        costType.setSource("自定义");
    }

    public static class StatusDisplayAndSource {
        String statusDisplay;
        String source;

        public StatusDisplayAndSource(String statusDisplay, String source) {
            this.statusDisplay = statusDisplay;
            this.source = source;
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
}
