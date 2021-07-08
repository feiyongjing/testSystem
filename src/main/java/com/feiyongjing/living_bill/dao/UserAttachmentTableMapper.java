package com.feiyongjing.living_bill.dao;

import org.apache.ibatis.annotations.Mapper;

import java.sql.Date;

@Mapper
public interface UserAttachmentTableMapper {

    Date getUserFirstCreatedBillTimeByUserId(long userId);
}
