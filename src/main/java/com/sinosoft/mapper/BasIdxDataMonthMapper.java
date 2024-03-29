package com.sinosoft.mapper;

import com.sinosoft.base.mapper.BaseMapper;
import com.sinosoft.model.BasIdxDataMonth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;
@Mapper
public interface BasIdxDataMonthMapper extends BaseMapper<BasIdxDataMonth> {
    /**
     * 删除对应的数据并批量插入新的数据
     * @param list 新数据
     * @return
     */
    int insertByBatch(@Param("list") List<BasIdxDataMonth> list);

    BasIdxDataMonth selectByKeys(@Param("dataDate")String dataDate,@Param("industry")String industry,@Param("index")String index,@Param("sysOrg")String sysOrg);

    int insertMonthDate(@Param("basIdxDataMonth")BasIdxDataMonth basIdxDataMonth,@Param("dataType")String type);

    void deleteMonthDate(@Param("dataType")String type);

    void insertByList(@Param("list")List<BasIdxDataMonth> basIdxDataMonthList);

    void insertByListFdl(@Param("list")List<BasIdxDataMonth> basIdxDataMonthList);

    void mergeData();

    void testMergeData();

    void insertCalIndexDatas(@Param("begDate")String begDate);
}