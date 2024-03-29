package com.sinosoft.service;

import com.sinosoft.base.service.BaseService;
import com.sinosoft.model.BasIndex;

import java.util.List;

public interface BasIndexService extends BaseService<BasIndex> {
    /**
     * 获取所有指标数据
     * @return
     */
    List<BasIndex> selectAllList();
    /**
     * 获取某一类指标
     * @return
     */
    List<BasIndex> selectByType(Long type);

    /**
     * 获取某一类指标
     * @return
     */
    List<BasIndex> selectByIndexType(Long type);

    /**
     * 根据指标名称获取id
     * @return
     */
    Long selectByIndexName(String tIndexName);

    /**
     * 根据产业名称查产业id
     * @param industryName
     * @return
     */
    Long selectBasIndustry(String industryName);
}
