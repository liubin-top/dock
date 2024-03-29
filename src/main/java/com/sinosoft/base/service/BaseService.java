package com.sinosoft.base.service;

import com.sinosoft.base.page.PageInfo;


public interface BaseService<T> {

    /**
     * 保存一个新的实体
     * @return 影响的行数 0失败，1成功
     */
    public int insert(T t);

    /**
     *
     * 更新一个实体
     * @return 影响的行数 0失败，1成功
     */
    public int updateByPrimaryKey(T t);

    /**
     *
     * 通过id删除实体
     * @param id
     * @return
     */
    public int deleteByPrimaryKey(Long[] id);

    /**
     *
     * 通过主键获取实体
     * @param id
     * @return
     */
    public T selectByPrimaryKey(Object id);

    /**
     * 分页查询
     * @return
     */
    public PageInfo pageList(PageInfo pageInfo);

}
