package com.sinosoft.mapper;

import com.sinosoft.base.mapper.BaseMapper;
import com.sinosoft.model.ProInvestment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProInvestmentMapper extends BaseMapper<ProInvestment> {

    void insertList(@Param("list") List<ProInvestment> proInvestments);
}