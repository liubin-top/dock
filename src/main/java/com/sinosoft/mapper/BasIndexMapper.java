package com.sinosoft.mapper;

import com.sinosoft.base.mapper.BaseMapper;
import com.sinosoft.model.BasIndex;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BasIndexMapper extends BaseMapper<BasIndex> {
    /**
     * 获取所有指标数据
     * @return
     */
    List<BasIndex> selectAllList();
    /**
     * 获取某一类指标
     * @return
     */
    List<BasIndex> selectByType(@Param("type")Long type);

    /**
     * 获取某一类指标
     * @return
     */
    List<BasIndex> selectByIndexType(@Param("type")Long type);

    /**
     * 根据指标名称获取id
     * @return
     */
    Long selectByIndexName(@Param("name")String tIndexName);

    /**
     * 根据产业名称查产业id
     * @return
     */
    Long selectBasIndustry(@Param("industryName")String industryName);
}