package com.sinosoft.service;

import com.sinosoft.base.service.BaseServiceImpl;
import com.sinosoft.mapper.BasIdxDataMapper;
import com.sinosoft.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class BasIdxDataServiceImpl extends BaseServiceImpl<BasIdxData> implements BasIdxDataService {
    @Autowired
    private BasIdxDataMapper basIdxDataMapper;

    @Override
    public List<BasIdxData> selectListForIndexMonth(HashMap searchMap) {
        return basIdxDataMapper.selectListForIndexMonth(searchMap);
    }

    @Override
    public int insertBasIdxData(BasIdxData basIdxData,String type) {
        return basIdxDataMapper.insertBasIdxData(basIdxData,type);
    }

    @Override
    public Integer deleteAllDate(String type) {
        return basIdxDataMapper.deleteAllDate(type);
    }

    @Override
    public void insertBasIdxDataData(CwData cwData) {
         basIdxDataMapper.insertBasIdxDataData(cwData);
    }

    @Override
    public void insertJykjJh(JykjDataJh jykjDataJh) {
        basIdxDataMapper.insertJykjJh(jykjDataJh);
    }

    @Override
    public void insertBasIdxDataJykjTj(JykjDataTj jykjDataTj) {
        basIdxDataMapper.insertBasIdxDataJykjTj(jykjDataTj);
    }

    @Override
    public void insertBasIdxDataYX(YXiao yXiao) {
        basIdxDataMapper.insertBasIdxDataYX(yXiao);
    }

}
