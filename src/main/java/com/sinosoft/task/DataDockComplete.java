package com.sinosoft.task;

import com.sinosoft.mapper.BasIdxDataMonthMapper;
import com.sinosoft.service.BasIdxDataService;
import com.sinosoft.util.ImportDataUtil;
import com.sinosoft.util.MergeDataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@EnableScheduling
public class DataDockComplete {

    @Autowired
    DataDockTask dataDockTask;

    @Autowired
    JykjStatisticsTask jykjStatisticsTask;
    @Autowired
    ElectricityMarketingTask electricityMarketingTask;

    @Autowired
    private BasIdxDataService basIdxDataService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ImportDataUtil importDataUtil;

    @Autowired
    MergeDataUtil mergeDataUtil;


    @Autowired
    private BasIdxDataMonthMapper basIdxDataMonthMapper;

//    @Scheduled(cron = " 0 00 00 1 4,7,10,1 ?")//每个季度的第一天零点进行统计此注解是每个季度结束后的下一天执行
    public void dataDockComplete() throws IOException, InterruptedException {
        //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
        /** 定时任务的名称作为key **/
        String key = "DataDockCompleteJob";
        /** 设置随机key **/
        String value = UUID.randomUUID().toString().replace("-", "");
        //如果键不存在则新增,存在则不改变已经有的值。(备注：失效时间要大于多台服务器之间的时间差，如果多台服务器时间差大于超时时间，定时任务可能会执行多次)
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, value, 60, TimeUnit.SECONDS);
        if (flag != null && flag) {
            System.out.println("{"+key+"} 锁定成功，开始处理业务");
            Integer integer = basIdxDataService.deleteAllDate("1");
            basIdxDataMonthMapper.deleteMonthDate("1");
            dataDockTask.dataDockJdjfQl();
            basIdxDataService.deleteAllDate("2");
            basIdxDataMonthMapper.deleteMonthDate("2");
            dataDockTask.dataDockJykjJh();
            basIdxDataService.deleteAllDate("3");
            basIdxDataMonthMapper.deleteMonthDate("3");
            jykjStatisticsTask.dataDockJdjf();
            basIdxDataService.deleteAllDate("4");
            basIdxDataMonthMapper.deleteMonthDate("4");
            electricityMarketingTask.dataDockJdjf();
            String lockValue = (String) redisTemplate.opsForValue().get(key);
            /** 只有：值未被释放(也就是当前未达到过期时间)，且是自己加锁设置的值(不要释放别人的所)，这种情况下才会释放锁 **/
            if (lockValue != null && lockValue.equals(value)) {
                redisTemplate.delete(key);
                System.out.println("{"+key+"} 解锁成功，结束处理业务");
            }
        } else {
            System.out.println("{"+key+"} 获取锁失败");
        }

    }


    public void test() throws InterruptedException, IOException, ParseException {
        //拉取财务数据
            dataDockTask.dataDockJdjfQl();
////
            dataDockTask.dataDockJykjtj();

//            dataDockTask.dataDockJykjJhFdl();
//            //合并数据
            mergeDataUtil.mergeData();

//           dataDockTask.dataDockJykjJhtest();

//           jykjStatisticsTask.dataDockJdjfTest();

//           electricityMarketingTask.dataDockJdjfTest();
//            dataDockTask.project();

    }

    public void dataData() throws IOException, InterruptedException {
        dataDockTask.dataJdjfQl();
//        dataDockTask.dataJykjJhtest();
//        dataDockTask.dataJykjJhFDLtest();
//        jykjStatisticsTask.dataJdjfTest();
//        electricityMarketingTask.dataJdjfTest();
    }

    public void localData() throws ParseException {
        dataDockTask.dataLocal();
        dataDockTask.dataLocalJykj();
        dataDockTask.dataLocalJykjJH();
        dataDockTask.dataLocalJykjFdlJH();
        mergeDataUtil.mergeData();
        mergeDataUtil.testMergeData();

    }

    public void importData() {
        importDataUtil.importData();
    }

    public void importDataTwo() {
        importDataUtil.importDataTwo();
    }

    public void importDataTwoOne() {
        importDataUtil.importDataTwoOne();
    }

    public void testjia() {
        dataDockTask.dataDockJdjf();
    }
}
