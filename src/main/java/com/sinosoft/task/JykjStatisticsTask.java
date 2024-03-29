package com.sinosoft.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sinosoft.mapper.BasIdxDataMonthMapper;
import com.sinosoft.model.BasIdxData;
import com.sinosoft.model.BasIdxDataMonth;
import com.sinosoft.model.JykjDataTj;
import com.sinosoft.model.SysOrg;
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
 * @date 2023/6/30
 */

@Component
@Transactional
public class JykjStatisticsTask {

    /**
     *吉电股份JYKJ统计数据
     */
    private static final String JYKJ_COUNT_URL="http://10.80.140.131/iuap-data-datafusion/openapi/asset/apply_jykj_tj_jdgf_info?token=QUtNdU9RLTJRc2ctQ3YzczNPQTRLTHFHajhhQm5XdGFqS1BMTGxDcDFuUFNSMEZVaHFENHVvVndyXzZyeHZOaA==";

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
            Integer total = sendGetTotal(JYKJ_COUNT_URL);
            LoggerFactory.getLogger(total.toString()).info("total:"+total);
            for (int j = 0; j <= (total/100) ; j++) {
                String urlPage = JYKJ_COUNT_URL+"&page="+j+"&size=100";
                JSONArray datas = sendGet(urlPage);
                LoggerFactory.getLogger(urlPage).info("url:"+JYKJ_COUNT_URL);
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
            Integer total = sendGetTotal(JYKJ_COUNT_URL);
            LoggerFactory.getLogger(total.toString()).info("total:"+total);
            for (int j = 0; j <= (total/100) ; j++) {
                String urlPage = JYKJ_COUNT_URL+"&page="+j+"&size=100";
                JSONArray datas = sendGet(urlPage);
                LoggerFactory.getLogger(urlPage).info("url:"+JYKJ_COUNT_URL);
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
            JSONObject dataJson = datas.getJSONObject(i);
            BasIdxData basIdxData = new BasIdxData();
            BasIdxDataMonth basIdxDataMonth = new BasIdxDataMonth();
            //获取组织id
            String orgStatementName = dataJson.getString("dim1");

            if("江西中电投新能源发电有限公司（母公司）".equals(orgStatementName)){
                orgStatementName = "江西中电投新能源发电有限公司（合并）";
            }
            if("北京吉能新能源科技有限公司".equals(orgStatementName)){
                orgStatementName = "北京吉能新能源科技有限公司（合并）";
            }
            if("安徽吉电新能源有限公司（母公司）".equals(orgStatementName)){
                orgStatementName = "安徽吉电新能源有限公司（合并）";
            }
            SysOrg sysOrg = new SysOrg();
            if (null == sysOrg.getId()) {
                sysOrg = sysOrgService.selectByStatementName(orgStatementName);
                if (null==sysOrg) {
                    sysOrg = sysOrgService.selectByOrgName(orgStatementName);
                    if (null==sysOrg){
                        continue;
                    }
                }
            }
            //获取指标id
            String tIndexName = dataJson.getString("dim8");
            if ("发电设备平均容量".equals(tIndexName)){
                tIndexName = "期末设备平均发电容量";
            }
            if ("期末发电设备容量".equals(tIndexName)){
                tIndexName = "期末设备发电容量";
            }
            Long basIndexId = basIndexService.selectByIndexName(tIndexName);
            LoggerFactory.getLogger("指标数据").info("指标："+tIndexName+",指标ID:"+basIndexId);
            //获取指标值
            BigDecimal indexValue = dataJson.getBigDecimal("c1_value");
            //获取产业类型
            String indurtyName = dataJson.getString("dim10");
            String region = dataJson.getString("dim9");
            String property = dataJson.getString("dim6");
            if (!("合计".equals(region))){
                continue;
            }
            if ("太阳能".equals(indurtyName)){
                indurtyName = "光伏";
            }
            if ("电力产业".equals(indurtyName)){
                indurtyName = "汇总";
            }
            Long basIndustry = basIndexService.selectBasIndustry(indurtyName);
            if ("本月".equals(property)){
                basIdxData.setProperty(3L);
                basIdxDataMonth.setDataMonth(indexValue);
            }
            if ("累计".equals(property)){
                basIdxData.setProperty(5L);
                basIdxDataMonth.setDataYear(indexValue);
            }
            basIdxData.setBasIndustry(basIndustry);
            basIdxData.setSysOrg(sysOrg.getId());
            basIdxData.setBasIndex(basIndexId);
            basIdxData.setDataValue(indexValue);
            basIdxData.setDataSource((byte)0);
            basIdxData.setDataDate(getDate(dataJson.getString("dim2"),dataJson.getString("dim4")));
            basIdxData.setProject(-1L);
            if (basIdxData.getBasIndex() == null) {
                continue;
            }
            basIdxDataService.insertBasIdxData(basIdxData,"3");

            //录入月数据
            basIdxDataMonth.setSysOrg(sysOrg.getId());
            basIdxDataMonth.setBasIndustry(basIdxData.getBasIndustry());
            basIdxDataMonth.setProject(basIdxData.getProject());
            basIdxDataMonth.setDataDate(getStrDate(dataJson.getString("dim2"),dataJson.getString("dim4")));
            basIdxDataMonth.setBasIndex(basIdxData.getBasIndex());
            basIdxDataMonth.setDataSource(0L);

            if (basIdxDataMonth.getDataYear()!=null
                    ||basIdxDataMonth.getDataYearPlan()!=null
                    ||basIdxDataMonth.getDataYearInit()!=null
                    ||basIdxDataMonth.getDataMonth()!=null
                    ||basIdxDataMonth.getDataMonthPlan()!=null
            ) {
                //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth,"3");
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

    public static Date getDate(String dim2,String dim4){
        dim2 = dim2.replace("年","-");
        dim4 = dim4.replace("月","");
        String strDate = dim2+dim4;
        DateFormat fmt = new SimpleDateFormat("yyyy-MM");
        Date date = new Date();
        try {
            date = fmt.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /***
     * 处理日期格式
     */
    public static String getStrDate(String dim2,String dim4){
        dim2 = dim2.replace("年","-");
        dim4 = dim4.replace("月","");
        int i = Integer.parseInt(dim4);
        if (i>=10) {
            return dim2+dim4;
        }
        return dim2+"0"+dim4;
    }

    public void dataJdjfTest() throws IOException {
        Integer total = sendGetTotal(JYKJ_COUNT_URL);
        LoggerFactory.getLogger(total.toString()).info("total:"+total);
        for (int j = 0; j <= (total/100) ; j++) {
            String urlPage = JYKJ_COUNT_URL+"&page="+j+"&size=100";
            JSONArray datas = sendGet(urlPage);
            LoggerFactory.getLogger(urlPage).info("url:"+JYKJ_COUNT_URL);
            processingDataDate(datas);
        }
    }

    private void processingDataDate(JSONArray datas) {
        for (int i = 0; i < datas.size(); i++) {
            JSONObject dataJson = datas.getJSONObject(i);
            //获取组织id
            String c1Category = dataJson.getString("c1_category");
            String c1Unit = dataJson.getString("c1_unit");
            String c1Value = dataJson.getString("c1_value");
            String dim1 = dataJson.getString("dim1");
            String dim10 = dataJson.getString("dim10");
            String dim1Code = dataJson.getString("dim1_code");
            String dim1Level = dataJson.getString("dim1_level");
            String dim1Mdcode = dataJson.getString("dim1_mdcode");
            String dim2 = dataJson.getString("dim2");
            String dim4 = dataJson.getString("dim4");
            String dim8 = dataJson.getString("dim8");
            Long basIndexId = basIndexService.selectByIndexName(dim8)==null?0:basIndexService.selectByIndexName(dim8);
            JykjDataTj jykjDataTj = new JykjDataTj(c1Category,c1Unit,c1Value,dim1,
                    dim10,dim1Code,dim1Level,dim1Mdcode,dim2,dim4,dim8,basIndexId);
            basIdxDataService.insertBasIdxDataJykjTj(jykjDataTj);

        }
    }
}
