package com.feiyongjing.living_bill.dao;

import com.feiyongjing.living_bill.enity.CostType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CostTypeMapper {
    Long insertCostType(CostType costType);

    List<CostType> getShownCostTypeByBillType(long userId, String billType, String statusDisplay);

    List<CostType> getAllCostTypeByBillType(long userId, String billType);

    CostType getCostTypeByCostTypeId(long id);

    void updateBillCostType(long id, String statusDisplay);

    void deleteBillCostType(long id);
}
