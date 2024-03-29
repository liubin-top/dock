package com.sinosoft.service;

import com.sinosoft.base.service.BaseService;
import com.sinosoft.model.*;

import java.util.HashMap;
import java.util.List;

public interface BasIdxDataService extends BaseService<BasIdxData> {
    /**
     * 综合查询-指标月数据查询
     * @param searchMap 检索条件
     * @return
     */
    List<BasIdxData> selectListForIndexMonth(HashMap searchMap);

    int insertBasIdxData(BasIdxData basIdxData,String type);

    Integer deleteAllDate(String type);

    void insertBasIdxDataData(CwData cwData);

    void insertJykjJh(JykjDataJh jykjDataJh);

    void insertBasIdxDataJykjTj(JykjDataTj jykjDataTj);

    void insertBasIdxDataYX(YXiao yXiao);

}
