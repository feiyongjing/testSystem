package com.feiyongjing.living_bill.service;

import com.feiyongjing.living_bill.controller.CostTypeController;
import com.feiyongjing.living_bill.dao.CostTypeMapper;
import com.feiyongjing.living_bill.enity.CostType;
import com.feiyongjing.living_bill.exception.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CostTypeService {
    private CostTypeMapper costTypeMapper;

    @Autowired
    public CostTypeService(CostTypeMapper costTypeMapper) {
        this.costTypeMapper = costTypeMapper;
    }

    public List<CostType> getShownCostTypeByBillType(long userId, String billType, String statusDisplay) {
        return costTypeMapper.getShownCostTypeByBillType(userId, billType, statusDisplay);
    }

    public List<CostType> getAllCostTypeByBillType(long userId, String billType) {
        return costTypeMapper.getAllCostTypeByBillType(userId, billType);
    }

    public CostType createBillCostType(CostType costType) {
        List<CostType> costTypes = getShownCostTypeByBillType(costType.getUserId(), costType.getBillType(), null);
        if (costTypes.stream().map(CostType::getBillCostType)
                .collect(Collectors.toList())
                .contains(costType.getBillCostType())) {
            throw HttpException.preconditionFailed("添加类型重复");
        }
        costTypeMapper.insertCostType(costType);
        return costTypeMapper.getCostTypeByCostTypeId(costType.getId());
    }

    public CostType updateBillCostType(long id, CostTypeController.StatusDisplayAndSource statusDisplayAndSource) {
        CostType costType1 = costTypeMapper.getCostTypeByCostTypeId(id);
        if (costType1 == null) {
            throw HttpException.preconditionFailed("需要修改的内容不存在");
        }
        if (!Objects.equals(UserContext.getCurrentUser().getId(), costType1.getUserId())) {
            throw HttpException.preconditionFailed("无权修改别人的账单费用类型");
        }
        if (statusDisplayAndSource.getSource().equals("默认")) {
            costTypeMapper.updateBillCostType(id, statusDisplayAndSource.getStatusDisplay());
        } else if (statusDisplayAndSource.getSource().equals("自定义")) {
            costTypeMapper.deleteBillCostType(id);
            return costType1;
        }
        return costTypeMapper.getCostTypeByCostTypeId(id);
    }
}
