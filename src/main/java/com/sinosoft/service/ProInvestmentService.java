package com.sinosoft.service;

import com.sinosoft.base.service.BaseService;
import com.sinosoft.model.ProInvestment;

import java.util.List;

public interface ProInvestmentService extends BaseService<ProInvestment> {
    void insertList(List<ProInvestment> proInvestments);
}
