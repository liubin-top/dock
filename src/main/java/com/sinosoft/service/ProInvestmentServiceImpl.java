package com.sinosoft.service;

import com.sinosoft.base.service.BaseServiceImpl;
import com.sinosoft.mapper.ProInvestmentMapper;
import com.sinosoft.model.ProInvestment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProInvestmentServiceImpl extends BaseServiceImpl<ProInvestment> implements ProInvestmentService{

    @Autowired
    ProInvestmentMapper proInvestmentMapper;

    @Override
    public void insertList(List<ProInvestment> proInvestments) {
        proInvestmentMapper.insertList(proInvestments);
    }
}
