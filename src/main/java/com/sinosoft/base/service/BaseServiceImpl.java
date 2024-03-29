package com.sinosoft.base.service;

//import com.github.pagehelper.Page;
//import com.github.pagehelper.PageHelper;
import com.sinosoft.base.mapper.BaseMapper;
import com.sinosoft.base.page.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@Service
public abstract class BaseServiceImpl<T> implements BaseService<T> {
    @Autowired
    private BaseMapper<T> baseMapper;//引入mapper泛型

    @Override
    public int insert(T t) {
        return  baseMapper.insert(t);
    }

    @Override
    public int updateByPrimaryKey(T t) {
        return  baseMapper.updateByPrimaryKey(t);
    }

    @Override
    public int deleteByPrimaryKey(Long[] id) {
        return  baseMapper.deleteByPrimaryKey(id);
    }

    @Override
    public T selectByPrimaryKey(Object id){
        return  baseMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageInfo pageList(PageInfo pageInfo) {
//        Page page = PageHelper.startPage(pageInfo.getPageNum(),pageInfo.getLimit());
//        List list = baseMapper.selectAll(pageInfo);
//        pageInfo.setTotal(page.getTotal());
//        pageInfo.setRows(list);
        return pageInfo;
    }

    /**
     * 获取父类的泛型，这里用于获取当前父类的泛型
     * @param clazz 子类 如SysDepServiceImpl
     * @return
     * @throws IndexOutOfBoundsException
     */
    private String getClassGenricType(Class clazz)
            throws IndexOutOfBoundsException {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class.getSimpleName();
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (!(params[0] instanceof Class)) {
            return Object.class.getSimpleName();
        }
        return ((Class) params[0]).getSimpleName();
    }

}
