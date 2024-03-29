package com.sinosoft.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sinosoft.mapper.BasIdxDataMonthMapper;
import com.sinosoft.model.BasIdxData;
import com.sinosoft.model.BasIdxDataMonth;
import com.sinosoft.model.SysOrg;
import com.sinosoft.model.YXiao;
import com.sinosoft.service.BasIdxDataService;
import com.sinosoft.service.BasIndexService;
import com.sinosoft.service.SysOrgService;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author zhanglinchuan
 * @description
 * @date 2023/7/6
 */

@Component
@Transactional
@EnableScheduling
public class ElectricityMarketingTask {
    /**
     *吉电股份电力营销数据
     */
    private static final String ELECTRICITY_MARKETING_URL="http://10.80.140.131/iuap-data-datafusion/openapi/asset/apply_jdgf_dyyx_sj?token=bZrmdRxa2Aw4erzX0U4CZj28pA6Rjmi7Nv5OhQFxBJxHH8CKGEQINJyHUsKiOjfw";

    @Autowired
    private SysOrgService sysOrgService;

    @Autowired
    private BasIndexService basIndexService;

    @Autowired
    private BasIdxDataService basIdxDataService;

    @Autowired
    private BasIdxDataMonthMapper basIdxDataMonthMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    public void dataDockJdjfTest() throws IOException, InterruptedException {
            Integer total = sendGetTotal(ELECTRICITY_MARKETING_URL);
            LoggerFactory.getLogger(total.toString()).info("total:"+total);
            for (int j = 0; j <= (total/100) ; j++) {
                String urlPage = ELECTRICITY_MARKETING_URL+"&page="+j+"&size=100";
                JSONArray datas = sendGet(urlPage);
                LoggerFactory.getLogger(urlPage).info("url:"+ELECTRICITY_MARKETING_URL);
                processingData(datas);
            }
    }

    //@Scheduled(cron = "0 0 0 15 * ?")
    public void dataDockJdjf() throws IOException, InterruptedException {
        /** 定时任务的名称作为key **/
        String key = "DataDockJdjf";
        /** 设置随机key **/
        String value = UUID.randomUUID().toString().replace("-", "");
        //如果键不存在则新增,存在则不改变已经有的值。(备注：失效时间要大于多台服务器之间的时间差，如果多台服务器时间差大于超时时间，定时任务可能会执行多次)
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, value, 60, TimeUnit.SECONDS);
        if (flag != null && flag) {
            System.out.println("{"+key+"} 锁定成功，开始处理业务");
            Integer total = sendGetTotal(ELECTRICITY_MARKETING_URL);
            LoggerFactory.getLogger(total.toString()).info("total:"+total);
            for (int j = 0; j <= (total/100) ; j++) {
                String urlPage = ELECTRICITY_MARKETING_URL+"&page="+j+"&size=100";
                JSONArray datas = sendGet(urlPage);
                LoggerFactory.getLogger(urlPage).info("url:"+ELECTRICITY_MARKETING_URL);
                processingData(datas);
            }
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

    public void processingData(JSONArray datas) {
        for (int i = 0; i < datas.size(); i++) {

//            JSONObject dataJson = datas.getJSONObject(i);
//            //获取组织id
//            String bgdj = dataJson.getString("bgdj");
//            String fschdlsr = dataJson.getString("fschdlsr");
//            String fscjydl = dataJson.getString("fscjydl");
//            String issuename = dataJson.getString("issuename");
//            String orgsname = dataJson.getString("orgsname");
//            String orgId = dataJson.getString("org_id");
//            String pjdj = dataJson.getString("pjdj");
//            String powertypename = dataJson.getString("powertypename");
//            String scjydj = dataJson.getString("scjydj");
//            String scjydl = dataJson.getString("scjydl");
//            String scjydlsr = dataJson.getString("scjydlsr");
//            YXiao yXiao = new YXiao(bgdj,fschdlsr,fscjydl,issuename,orgsname,orgId,pjdj,powertypename,scjydj,scjydl,scjydlsr);
//            basIdxDataService.insertBasIdxDataYX(yXiao);


            JSONObject dataJson = datas.getJSONObject(i);
            BasIdxData basIdxData = new BasIdxData();
            //获取组织id
            String orgSimpleName = dataJson.getString("orgsname");
            SysOrg sysOrg = sysOrgService.selectBySimpleName(orgSimpleName);
            if (null == sysOrg) {
                continue;
            }
            //获取产业类型
            Long basIndustry = basIndexService.selectBasIndustry(dataJson.getString("powertypename"));
            basIdxData.setBasIndustry(basIndustry);
            basIdxData.setProperty(3L);
            basIdxData.setSysOrg(sysOrg.getId());
            basIdxData.setDataSource((byte)19);
            basIdxData.setDataDate(getDate(dataJson.getString("issuename")));
            basIdxData.setProject(-1L);
            //指标为标杆电价
            Long basIndexId = basIndexService.selectByIndexName("标杆电价");
            basIdxData.setBasIndex(basIndexId);
            if (null != basIdxData.getBasIndex()) {
                BigDecimal indexValue = dataJson.getBigDecimal("bgdj");
                basIdxData.setDataValue(indexValue);
                basIdxDataService.insertBasIdxData(basIdxData,"4");
            }
            //指标为非市场化交易电量
             basIndexId = basIndexService.selectByIndexName("非市场化交易电量");
            basIdxData.setBasIndex(basIndexId);
            if (null != basIdxData.getBasIndex()) {
                BigDecimal indexValue = dataJson.getBigDecimal("fscjydl");
                basIdxData.setDataValue(indexValue);
                basIdxDataService.insertBasIdxData(basIdxData,"4");
            }
            //指标为市场化交易电价
             basIndexId = basIndexService.selectByIndexName("市场化交易电价");
            basIdxData.setBasIndex(basIndexId);
            if (null != basIdxData.getBasIndex()) {
                BigDecimal indexValue = dataJson.getBigDecimal("scjydj");
                basIdxData.setDataValue(indexValue);
                basIdxDataService.insertBasIdxData(basIdxData,"4");
            }
            //指标为市场化交易电量
             basIndexId = basIndexService.selectByIndexName("市场化交易电量");
            basIdxData.setBasIndex(basIndexId);
            if (null != basIdxData.getBasIndex()) {
                BigDecimal indexValue = dataJson.getBigDecimal("scjydl");
                basIdxData.setDataValue(indexValue);
                basIdxDataService.insertBasIdxData(basIdxData,"4");
            }
            //指标为市场化交易电力收入
             basIndexId = basIndexService.selectByIndexName("市场化交易电力收入");
            basIdxData.setBasIndex(basIndexId);
            if (null != basIdxData.getBasIndex()) {
                BigDecimal indexValue = dataJson.getBigDecimal("scjydlsr");
                basIdxData.setDataValue(indexValue);
                basIdxDataService.insertBasIdxData(basIdxData,"4");
            }


            //录入月数据
            BasIdxDataMonth basIdxDataMonth = new BasIdxDataMonth();
            basIdxDataMonth.setSysOrg(sysOrg.getId());
            basIdxDataMonth.setBasIndustry(basIdxData.getBasIndustry());
            basIdxDataMonth.setProject(-1L);
            basIdxDataMonth.setDataDate(getStrDate(dataJson.getString("issuename")));
            basIdxDataMonth.setDataSource(19L);
            //指标为标杆电价
            basIndexId = basIndexService.selectByIndexName("标杆电价");
            basIdxDataMonth.setBasIndex(basIndexId);
            if (null != basIdxDataMonth.getBasIndex()) {
                BigDecimal indexValue = dataJson.getBigDecimal("bgdj");
                basIdxDataMonth.setDataMonth(indexValue);
            }
            if (basIdxDataMonth.getDataYear()!=null
                    ||basIdxDataMonth.getDataYearPlan()!=null
                    ||basIdxDataMonth.getDataYearInit()!=null
                    ||basIdxDataMonth.getDataMonth()!=null
                    ||basIdxDataMonth.getDataMonthPlan()!=null
            ) {
                //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth,"4");
            }
            //指标为非市场化交易电量
            basIndexId = basIndexService.selectByIndexName("非市场化交易电量");
            basIdxDataMonth.setBasIndex(basIndexId);
            if (null != basIdxDataMonth.getBasIndex()) {
                BigDecimal indexValue = dataJson.getBigDecimal("fscjydl");
                basIdxDataMonth.setDataMonth(indexValue);
            }
            if (basIdxDataMonth.getDataYear()!=null
                    ||basIdxDataMonth.getDataYearPlan()!=null
                    ||basIdxDataMonth.getDataYearInit()!=null
                    ||basIdxDataMonth.getDataMonth()!=null
                    ||basIdxDataMonth.getDataMonthPlan()!=null
            ) {
                //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth,"4");
            }
            //指标为市场化交易电价
            basIndexId = basIndexService.selectByIndexName("市场化交易电价");
            basIdxDataMonth.setBasIndex(basIndexId);
            if (null != basIdxDataMonth.getBasIndex()) {
                BigDecimal indexValue = dataJson.getBigDecimal("scjydj");
                basIdxDataMonth.setDataMonth(indexValue);
            }
            if (basIdxDataMonth.getDataYear()!=null
                    ||basIdxDataMonth.getDataYearPlan()!=null
                    ||basIdxDataMonth.getDataYearInit()!=null
                    ||basIdxDataMonth.getDataMonth()!=null
                    ||basIdxDataMonth.getDataMonthPlan()!=null
            ) {
                //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth,"4");
            }
            //指标为市场化交易电量
            basIndexId = basIndexService.selectByIndexName("市场化交易电量");
            basIdxDataMonth.setBasIndex(basIndexId);
            if (null != basIdxDataMonth.getBasIndex()) {
                BigDecimal indexValue = dataJson.getBigDecimal("scjydl");
                basIdxDataMonth.setDataMonth(indexValue);
            }
            if (basIdxDataMonth.getDataYear()!=null
                    ||basIdxDataMonth.getDataYearPlan()!=null
                    ||basIdxDataMonth.getDataYearInit()!=null
                    ||basIdxDataMonth.getDataMonth()!=null
                    ||basIdxDataMonth.getDataMonthPlan()!=null
            ) {
                //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth,"4");
            }
            //指标为市场化交易电力收入
            basIndexId = basIndexService.selectByIndexName("市场化交易电力收入");
            basIdxDataMonth.setBasIndex(basIndexId);
            if (null != basIdxDataMonth.getBasIndex()) {
                BigDecimal indexValue = dataJson.getBigDecimal("scjydlsr");
                basIdxDataMonth.setDataMonth(indexValue);
            }
            if (basIdxDataMonth.getDataYear()!=null
                    ||basIdxDataMonth.getDataYearPlan()!=null
                    ||basIdxDataMonth.getDataYearInit()!=null
                    ||basIdxDataMonth.getDataMonth()!=null
                    ||basIdxDataMonth.getDataMonthPlan()!=null
            ) {
                //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth,"4");
            }
        }
    }

    /**
     * 发送get请求获取数据
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static JSONArray sendGet(String url) throws IOException {
        CloseableHttpClient httpClient;
        HttpGet httpGet;
        String CONTENT_TYPE = "Content-Type";
        httpClient = HttpClients.createDefault();
        httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        String resp;
        try {
            HttpEntity entity = response.getEntity();
            resp = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        JSONObject json = JSONObject.parseObject(resp);
        JSONArray data = json.getJSONArray("data");
        return data;
    }

    /**
     * 发送get请获取数据总量
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static Integer sendGetTotal(String url) throws IOException {
        String page = "&page=1&size=10";
        url = url+page;
        CloseableHttpClient httpClient;
        HttpGet httpGet;
        String CONTENT_TYPE = "Content-Type";
        httpClient = HttpClients.createDefault();
        httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        String resp;
        try {
            HttpEntity entity = response.getEntity();
            resp = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        JSONObject json = JSONObject.parseObject(resp);
        Integer totalElements = json.getInteger("totalElements");
        return totalElements;
    }

    /***
     * 处理日期格式
     */
    public static Date getDate(String issuename){
        issuename = issuename.replace("年","-");
        issuename = issuename.replace("月","");
        DateFormat fmt = new SimpleDateFormat("yyyy-MM");
        Date date = new Date();
        try {
            date = fmt.parse(issuename);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /***
     * 处理日期格式
     */
    public static String getStrDate(String issuename){
        issuename = issuename.replace("年","-");
        issuename = issuename.replace("月","");
        return issuename;
    }

    public void dataJdjfTest() throws IOException {
        Integer total = sendGetTotal(ELECTRICITY_MARKETING_URL);
        LoggerFactory.getLogger(total.toString()).info("total:"+total);
        for (int j = 0; j <= (total/100) ; j++) {
            String urlPage = ELECTRICITY_MARKETING_URL+"&page="+j+"&size=100";
            JSONArray datas = sendGet(urlPage);
            LoggerFactory.getLogger(urlPage).info("url:"+ELECTRICITY_MARKETING_URL);
            processingDataDate(datas);
        }
    }

    private void processingDataDate(JSONArray datas) {
        for (int i = 0; i < datas.size(); i++) {
            JSONObject dataJson = datas.getJSONObject(i);
            //获取组织id
            String bgdj = dataJson.getString("bgdj");
            String fschdlsr = dataJson.getString("fschdlsr");
            String fscjydl = dataJson.getString("fscjydl");
            String issuename = dataJson.getString("issuename");
            String orgsname = dataJson.getString("orgsname");
            String orgId = dataJson.getString("org_id");
            String pjdj = dataJson.getString("pjdj");
            String powertypename = dataJson.getString("powertypename");
            String scjydj = dataJson.getString("scjydj");
            String scjydl = dataJson.getString("scjydl");
            String scjydlsr = dataJson.getString("scjydlsr");
            YXiao yXiao = new YXiao(bgdj,fschdlsr,fscjydl,issuename,orgsname,orgId,pjdj,powertypename,scjydj,scjydl,scjydlsr);
            basIdxDataService.insertBasIdxDataYX(yXiao);
        }
    }
}
