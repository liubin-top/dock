package com.sinosoft.service;

import com.sinosoft.base.service.BaseServiceImpl;
import com.sinosoft.mapper.CwDataMapper;
import com.sinosoft.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CwDataServiceImpl extends BaseServiceImpl<CwData> implements CwDataService{

    @Autowired
    CwDataMapper cwDataMapper;
    /**
     * 获取所有指标数据
     * @return
     */
    public List<CwData> selectPgaeList(int page1,int size1,String tableName){
        int size = size1;
        int page = (page1-1)*size;
        return cwDataMapper.selectPgaeList(page,size,tableName);
    }

    @Override
    public int selectAllCount(String tableName) {
        return cwDataMapper.selectAllCount(tableName);
    }

    @Override
    public int selectAllCountJyKjTj(String tableName) {
        return cwDataMapper.selectAllCountJyKjTj(tableName);
    }

    @Override
    public List<JykjDataTjLocal> selectPgaeListJyKjTj(int page1, int size1, String tableName) {
        int size = size1;
        int page = (page1-1)*size;
        return cwDataMapper.selectPgaeListJyKjTj(page,size,tableName);
    }

    @Override
    public List<JykjDataJh> selectPgaeListJyKjJH(int page1, int size1, String tableName) {
        int size = size1;
        int page = (page1-1)*size;
        return cwDataMapper.selectPgaeListJyKjJH(page,size,tableName);
    }

    @Override
    public List<JykjFdlJh> selectPgaeListJyKjFdl(int page1, int size1, String tableName) {
        int size = size1;
        int page = (page1-1)*size;
        return cwDataMapper.selectPgaeListJyKjFdl(page,size,tableName);
    }
}
