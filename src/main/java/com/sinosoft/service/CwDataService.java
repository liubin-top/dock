package com.sinosoft.service;

import com.sinosoft.base.service.BaseService;
import com.sinosoft.model.CwData;
import com.sinosoft.model.JykjDataJh;
import com.sinosoft.model.JykjDataTjLocal;
import com.sinosoft.model.JykjFdlJh;

import java.util.List;

public interface CwDataService extends BaseService<CwData> {
    List<CwData> selectPgaeList(int page,int size,String tableName);
    int selectAllCount(String tableName);

    int selectAllCountJyKjTj(String s);

    List<JykjDataTjLocal> selectPgaeListJyKjTj(int page, int size, String tableName);

    List<JykjDataJh> selectPgaeListJyKjJH(int j, int i, String s);

    List<JykjFdlJh> selectPgaeListJyKjFdl(int j, int i, String s);
}
