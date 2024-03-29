package com.sinosoft.util;

import com.sinosoft.base.page.PageInfo;
import com.sinosoft.mapper.BasIdxDataMonthMapper;
import com.sinosoft.model.BasIdxDataMonth;
import com.sinosoft.service.BasIdxDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class MergeDataUtil {

    @Autowired
    private BasIdxDataMonthMapper basIdxDataMonthMapper;

    public void mergeData(){
        basIdxDataMonthMapper.mergeData();
//        basIdxDataMonthMapper.insertCalIndexDatas(new SimpleDateFormat("yyyy-MM").format(new Date()));
        basIdxDataMonthMapper.insertCalIndexDatas("2024-02");
    }

    public void delRepeat() {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setLimit(10000000);
      List<BasIdxDataMonth> months = basIdxDataMonthMapper.selectAll(pageInfo);
      basIdxDataMonthMapper.insertByList(months);
    }

    public void testMergeData() {
        basIdxDataMonthMapper.testMergeData();

    }
}
