package com.sinosoft.mapper;

import com.sinosoft.base.mapper.BaseMapper;
import com.sinosoft.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

public interface BasIdxDataMapper extends BaseMapper<BasIdxData> {
    /**
     * 综合查询-指标月数据查询
     * @param searchMap 检索条件
     * @return
     */
    List<BasIdxData> selectListForIndexMonth(@Param("searchMap")HashMap searchMap);

    int insertBasIdxData(@Param("basIdxData")BasIdxData basIdxData,@Param("dataType")String type);

    Integer deleteAllDate(@Param("dataType")String type);

    void insertBasIdxDataData(@Param("cwData")CwData cwData);

    void insertJykjJh(@Param("jykjDataJh")JykjDataJh jykjDataJh);

    void insertBasIdxDataJykjTj(@Param("jykjDataTj")JykjDataTj jykjDataTj);

    void insertBasIdxDataYX(@Param("yXiao")YXiao yXiao);
}