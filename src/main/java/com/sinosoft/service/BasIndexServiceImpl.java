package com.sinosoft.service;

import com.sinosoft.base.service.BaseServiceImpl;
import com.sinosoft.mapper.BasIndexMapper;
import com.sinosoft.model.BasIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BasIndexServiceImpl extends BaseServiceImpl<BasIndex> implements BasIndexService {
    @Autowired
    private BasIndexMapper basIndexMapper;

    @Override
    public List<BasIndex> selectAllList() {
        return basIndexMapper.selectAllList();
    }

    @Override
    public List<BasIndex> selectByType(Long type) {
        return basIndexMapper.selectByType(type);
    }

    @Override
    public List<BasIndex> selectByIndexType(Long type) {
        return basIndexMapper.selectByIndexType(type);
    }

    @Override
    public Long selectByIndexName(String tIndexName) {
        return basIndexMapper.selectByIndexName(tIndexName);
    }

    @Override
    public Long selectBasIndustry(String industryName) {
        return basIndexMapper.selectBasIndustry(industryName);
    }
}
