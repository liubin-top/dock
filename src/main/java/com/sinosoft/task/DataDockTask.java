package com.sinosoft.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sinosoft.mapper.BasIdxDataMonthMapper;
import com.sinosoft.model.*;
import com.sinosoft.service.*;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

@Component
@EnableScheduling
public class DataDockTask {

    //吉电股份JYKJ计划数据
    private static final String JYKJ_PLAN_URL="http://10.104.0.125/iuap-data-datafusion/openapi/asset/apply_jykj_jh_jdgf_info?token=NjJvUktQdDdWNy1qLXZWVUNsdTc4RnVnd2tVaTVyYmZ5Q0pkSlBGdFlKdk84UVFtQjAxdWE3ZlBGSEpMaWlqTw==";
    //吉电股份JYKJ统计数据
    private static final String JYKJ_COUNT_URL="http://10.104.0.125/iuap-data-datafusion/openapi/asset/api_ads_apply_jykj_tj_jdgf_info_mf?token=S2t2ZGhZZUhJUVZ0UmRqUVk5eXpMWlE5Yk9GLTI3Q0NSYno0WlluVEZGRnNwT1lYUVVwbUY3TFhndXFDWnhQaw==";
    //计划发电量数据
    private static final String JYKJ_JH_FDL = "http://10.104.0.125/iuap-data-datafusion/openapi/asset/api_ads_apply_jykj_jh_fdl_jdgf_info?token=dE1sNktwTzdjbjNCeTJQRDg0TGZMNFJ4Y2t1cklRTXNhOUszUzJzRmVqSnh1SE9HWHFCRDdZc3FJa3poZjVMaA==";

    //财务报表
    private static final String URL = "http://10.104.0.125/iuap-data-datafusion/openapi/asset/api_a_apply_jd_ygcw_report_indexvalue_";
    //应收账款统计表(月报表)
    private static final String YSZKTJ = "yszktj_mf?token=RVIzZFd6di1BOXNLODhPeExKdzVPVHBnckw1Ty1kR3R6Rm1MN0hfcXBVUnF6U0licnRxZDVheEdUaWF2eTlpOA==";
    //主要财务绩效指标分析表
    private static final String ZYCWJXZB = "zycwjxzb_mf?token=RERHMUtYNG43TnEzMW1KZUxHMi1hRFhMdnFkSloyZVg4QUs5SmhxZHlhZG1rQWhzOUZ1MHFZRUUyNEZTMkxKUQ==";
    //经济增加值（EVA）计算表（季度报表）
//    private static final String JJZJZ = "jjzjz?token=MFFiYnRPUVdicmhGR1RaOE9UMGhkWTB2VmVsYklYN0tDaWNaR19BWmw3TU5HOGRuc0tDZVZjXzhfMDZCbUxiRQ==";
//    //资产负债月报表
    private static final String ZCFZ = "zcfz_mf?token=bW1MVmZuXzRtdWp0NDB4VEdSV1dsN1prdUpBMkVEdktJZ05yeWh4aHVEQVBZY0xVUkFKaG52OUhNRzhLR0NZRQ==";
    //企业基本情况月报表
//    private static final String QYJBQK = "qyjbqk?token=eFBMOExiZWJTSlFSdnRFMXNqNVpTRU00c1lEM1BXLVdvS0hqNkg0bHBzWnRrWm9TZC1OZkU5R2Y0NWhGUnF2Tw==";
    //存货统计表（月报表）
    private static final String CHTJ = "chtj_mf?token=bnRmWnRjMmtZVUVLNFpnbktUSWU5UVRxSDh0RUdiWllGX1VaRlE5b3lvWEg2eUZ5M2xWVkVoN1M1dkZDVWZQRA==";
    //煤、铝技术经济指标月报表（未使用）
//    private static final String MLJSJJZB = "mljsjjzb?token=ekhEb2R2c0RDSTZ0bElOWUJwYkUwX1BMMEt2YWFqNTlXakVMc3E0cmdvelpCbWR6RGRJTHdEMWpzMnI2VFMwYg==";
//    //利润月报表（总）
    private static final String LR = "lr_mf?token=dnQwaGJhbTJSLUxDLW5DRnVGVXVvX1lnMWR1MXFrRXpJRVpBblNOWkRNczVTTXFfbmJPU2htc3FZV1EtdUNDYw==";
    //盈利能力指标分析表
//    private static final String YLNLZBX = "ylnlzb_mf?token=WGhUUnVNeXI1eTZHRFFXV0I4UGVFZG13b0tvY0p5amwwZjhOYWFrbmxqaHFCNUJXa1QzZUl6VE9xYm9rczh2Vw==";
//    //成本情况月报表
    private static final String CBQK = "cbqk_mf?token=RmJUYjBDUWlLVzFZZlJGTVRoYy1YTmpPZDlYcDhoNl9RbkRtUkN0dHR4cUN3cUo0VVRza2VkU0NPeHhDTDNFQg==";
    //电力技术经济指标月报表
    private static final String DLJSJJZB = "dljsjjzb_mf?token=eXhfdm1ER2gyV3FIYlRXMFZSWlpTTENEMkdqekZJTEdURlpJU1pDUnpCZWNscThfcnJmWS03UGJVdm1UWnVCYQ==";
    //利润月报表（一）
    private static final String LRONE = "lr_one_mf?token=VVBYN0N1SVJUWE1SYUtUWmUxVUM4VDJMN1I3ejhvbmdCVEFBOU02Z3pUaTh6clg1OVJiOF9SaUV6MDRzVzFMaA==";
    //利润月报表（三）
    private static final String LRTHREE = "lr_three_mf?token=b0wzZ3g5X1pqQnJsXzVldjRQUFluc0lvdzRORjZvRkFHTFpXT3NxRjlIaDZwVmR6M1Q4aldYZzN5dmdlUEJJaw==";
    //带息负债情况表（月报表）
    private static final String DXFZQK = "dxfzqk_mf?token=LTR1dWRuWm9LQ19Qdkt1QUlxbW9ZYWczaEVKckQycXNnSHFYMzJWYURPdm9lXzFITE5HOU5VcWk3VENMR2REQg==";



    //应收账款统计表（月报表）增量数据：
    private static final String YSZKTJ_MF = "yszktj_mf?token=Ajr4Rr0Jj4cyBXJkS29Kq-5SKgrlnnn0huwkZorpJBATaHY3NlshLm4_c94wDPOY";
    //主要财务绩效指标分析表增量数据：
    private static final String ZYCWJXZB_MF = "zycwjxzb_mf?token=1-HU1Bdt_w9BmFSz4pMM8PiT2x6fDPiVUxJ3CkV6q6peOUpAno3PV4xD_rfSuIYP";
    //经济增加值（EVA）计算表（季报表）增量数据数据：
    private static final String JJZJZ_MF = "jjzjz_mf?token=C_cyff793zaowmoatIPFmWo__tUPeY5xmgxO9zn7pXnleBeYHNR3lKgnVvgPL_EX";
    //资产负债月报表 增量数据
    private static final String ZCFZ_MF = "zcfz_mf?token=4M_6H9aJ-hDFdUI7sOIumdKTzYgsw7SAF69HTiLMQagod6fI2R2Kg89ADQbEefK-";
    //企业基本情况月报表增量数据：
//    private static final String QYJBQK_MF = "qyjbqk_mf?token=QDxDyPmGGJTK8zvhZGYCNIIA2791WCcOrwFQsyNPTLwu482cE7a7y_s5rJUS9F3O";
    //存货统计表（月报表）增量数据：
    private static final String CHTJ_MF ="chtj_mf?token=yesm5Ilnh9faCBVa8r_6NC8OShNAk-x6ewiszkWo0ww9S6le1hX5Hki-rLxlSJlb";
    //煤、铝技术经济指标月报表增量数据：
    private static final String MLJSJJZB_MF = "mljsjjzb_mf?token=-u6GwnbSf1Cz9I8rY6SCj7gkwJWAdVh-Ce0Kf2kAypLC6dXQWZQvP73RoVvJMeKF";
    //利润月报表（总）增量数据：
    private static final String LR_MF = "lr_mf?token=epCLGlMmPUKrvRTau2Qbd4WLSkfEIKwGZ3rXSXnzieG_gr4gc9vpd_BvWYVtPeXy";
    //盈利能力指标分析表增量数据：
    private static final String YLNLZBX_MF = "ylnlzb_mf?token=EH7T-pA0Fb9mFg2vS6DFrKYr60h-GB0Wp4U6i-nPZgSPj5gDfpSb5Erc9MI3A98x";
    //成本情况月报表增量数据：
    private static final String CBQK_MF = "cbqk_mf?token=daro5NgioYlrC6zdfyqOEiTz1673hYTFAxlCA6PVFfSMnIh92NI-Z1wAHSn5nkDo";
    //电力技术经济指标月报表增量数据：
    private static final String DLJSJJZB_MF = "dljsjjzb_mf?token=JOq20WUF0YLLNu7yij1Mo01Y9oguPP718vlNW2Ifs7KS53SxfkLMABkWtF8ROTg6";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SysOrgService sysOrgService;

    @Autowired
    private BasIndexService basIndexService;

    @Autowired
    private BasIdxDataService basIdxDataService;

    @Autowired
    private BasIdxDataMonthMapper basIdxDataMonthMapper;

    @Autowired
    private ProInvestmentService proInvestmentService;

    @Autowired
    CwDataService CwDataService;

    private static final Map<String, String> changeMap = new HashMap<String, String>();
    static {

        changeMap.put("工资_管理费用","管理费用");
        changeMap.put("资产负债率（%）","资产负债率");
        changeMap.put("资产总计","资产总额");
        changeMap.put("负债合计","负债总额");
        changeMap.put("期末装机容量","期末设备发电容量");
        changeMap.put("平均装机容量","期末设备平均发电容量");
        changeMap.put("营业税金及附加","税金及附加");
        changeMap.put("营业利润率（%）","营业利润率");
        changeMap.put("EVA值","EVA");
        changeMap.put("利润增长率（%）","利润增长率");
        changeMap.put("存货周转率（次）","存货周转率");
        changeMap.put("应收账款周转率（次）","应收账款周转率");
        changeMap.put("外购电费用","外购电力费");
        changeMap.put("售热平均单价(含税)","售热单价");
        changeMap.put("长期应收款","一年以上应收账款");
        changeMap.put("应收新能源电价补贴","新能源电价补贴");
        changeMap.put("总资产周转率（次）","总资产周转率");
        changeMap.put("流动资产周转率（次）","流动资产周转率");
        changeMap.put("应收账款增长率（%）","应收账款增长率");
        changeMap.put("资本积累率（%）","资本积累率");
        changeMap.put("资产增长率（%）","资产增长率");
        changeMap.put("带息负债比率（%）","带息负债比率");
        changeMap.put("按账龄1年以内","1年以内应收账款金额");
        changeMap.put("按账龄1-2年","1-2年应收账款金额");
        changeMap.put("按账龄2-3年","2-3年应收账款金额");
        changeMap.put("按账龄3-4年","3-4年应收账款金额");
        changeMap.put("按账龄4-5年","4-5年应收账款金额");
        changeMap.put("按账龄5年以上","5年以上应收账款金额");
        changeMap.put("减:坏帐准备","坏账准备");
        changeMap.put("所有者权益（或股东权益）","股东权益");
        changeMap.put("发电标煤耗量","发电耗标煤量");
        changeMap.put("备件","备品备件");
        changeMap.put("发电供热标煤单价","发电供热标准煤单价");
        changeMap.put("民用售热量","售热量-民用");
        changeMap.put("工业售热量","售热量-工业用");
        changeMap.put("工业平均售热单价(含税)","售热单价-工业用");
        changeMap.put("民用平均售热单价(含税)","售热单价-民用");
        changeMap.put("利息支出净额","利息费用");
        changeMap.put("归属母公司净资产收益率","净资产收益率（不含少数股东权益）");
        changeMap.put("1、资产总额","资产总额");//没有 相识 平均资产总额
        changeMap.put("2、负债总额","负债总额");//没有
        changeMap.put("流动资产","流动资产总额");
        changeMap.put("非流动资产","非流动资产总额");
        changeMap.put("流动负债","流动负债总额");
        changeMap.put("非流动负债","非流动负债总额");
        changeMap.put("热管道损失","供热管道损失");
        changeMap.put("成本费用利润率（%）","成本费用利润率");
        changeMap.put("成本费用总额占营业收入的比率（%）","成本费用总额占营业收入的比率");
        changeMap.put("存货增长率（%）","存货增长率");
        changeMap.put("短期借款占全部借款的比率（%）","短期借款占全部借款的比率");
        changeMap.put("营业收入增长率（%）","营业收入增长率");
        changeMap.put("利用小时","平均利用小时");//需要判断s_index_name
        changeMap.put("购电量","购入电量");
        changeMap.put("发电标煤单价","发电标准煤单价");
        changeMap.put("供热标煤耗量","供热标准煤量");
        changeMap.put("供热标煤单价","供热标准煤单价");
        changeMap.put("40、发电供热用天然气体量","发电供热用天然气体量");//没有
        changeMap.put("天然煤平均单价","天然气平均单价");
        changeMap.put("其中:(1)短期借款利息支出","短期借款利息支出");
        changeMap.put("(2)长期借款利息支出","长期借款利息支出");
        changeMap.put("主营业务收入增长率（%）","主营业务收入增长率");//重复
        changeMap.put(" 其中：在职职工人数","在职职工人数");
        changeMap.put("固定资产投资额","固定资产");
        changeMap.put("燃料","存货-燃料");
        changeMap.put("材料","存货-材料");
        changeMap.put("工程施工","存货-工程施工");
        changeMap.put("低值易耗品","存货-低值易耗品");
        changeMap.put("预付账款","预付账款");

    }

    public String findIndex(String tName,String sName){
        if ("生产成本".equals(tName)){
            if ("三、煤电成本".equals(sName)){
                tName = "煤电成本";
            }
            if ("五、风电成本".equals(sName)){
                tName ="风电成本";
            }
            if ("六、太阳能成本".equals(sName)){
                tName ="太阳能成本";
            }
            if ("电力总成本".equals(sName)){
                tName ="电力总成本";
            }
            if ("八、供热成本".equals(sName)){
                tName ="供热成本";
            }
        }
        if ("7、厂供电量".equals(sName)){
            tName = "厂供电量";
        }
        if ("研发费用".equals(sName)){
            tName = "研发费用";
        }
        if ("1.净资产收益率（含少数股东权益）（%）".equals(sName)){
            tName = "净资产收益率（含少数股东权益）";
        }
        if ("所有者权益合计".equals(sName)){
            tName = "所有者权益";
        }
        String s = changeMap.get(tName);
        if (StringUtils.isBlank(s)){
            return tName;
        }

        return s;
    }

    public void dataDockJdjfQl() throws IOException, InterruptedException {
        List<String> listUrl = new ArrayList<>();
        listUrl.add(YSZKTJ);
        listUrl.add(ZYCWJXZB);
        listUrl.add(ZCFZ);
        listUrl.add(CHTJ);
        listUrl.add(DXFZQK);
        listUrl.add(LR);
        listUrl.add(LRONE);
        listUrl.add(LRTHREE);
        listUrl.add(CBQK);
        listUrl.add(DLJSJJZB);
        for (int i = 0; i < listUrl.size(); i++) {
            String url;
            url = URL + listUrl.get(i);
            Integer total = null;
            try {
                total = sendGetTotal(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            LoggerFactory.getLogger(total.toString()).info("total:" + total);
            for (int j = 0; j <= total/1000; j++) {
                String urlPage = url + "&page=" + j + "&size=1000";
                JSONArray datas = null;
                try {
                    datas = sendGet(urlPage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LoggerFactory.getLogger(urlPage).info("url:" + url);
                try {
                    processingData(datas);
                } catch (InterruptedException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }




    }

//    public void dataDockJdjfQlLocal() {
//        int total = CwDataService.selectAllCount();
//        System.out.println("总计："+total);
//        System.out.println("页数："+total/100);
//        int k = (total/100)/16;
//        int n = 0;
//        int old =1;
//        ExecutorService executorServiceDataDockLock = new ThreadPoolExecutor(16, 16, 0,
//                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
//        for (int i = 1; i < 17; i++) {
//            if (i!=1){
//                old =n+1;
//            }
//            n=n+k;
//            int nw = n;
//            if (i==16){
//                nw = total/100+1;
//            }
//            executorServiceDataDockLock.execute(new DataDockLocalTread(old,nw));//填充-基本参数
//
//        }
//        executorServiceDataDockLock.shutdown();//执行线程
//        while (true) {
//            if (executorServiceDataDockLock.isTerminated()) {//线程池执行完毕
//                break;
//            }
//        }
//
//
//    }
    public void dataLocal() throws ParseException {
        List<String> listUrl = new ArrayList<>();
        //成本情况月报表
        listUrl.add("apply_jdgf_ygcw_report_indexvalue_cbqk_910304");
        //存货统计表
        listUrl.add("apply_jdgf_ygcw_report_indexvalue_chtj_911974");
//        //电力技术经济指标月报表
        listUrl.add("apply_jdgf_ygcw_report_indexvalue_dljsjjzb_910305");
//        //带息负债情况表
        listUrl.add("apply_jdgf_ygcw_report_indexvalue_dxfzqk_911970");
//        //经济增加值（EVA）计算表
//        listUrl.add("apply_jdgf_ygcw_report_indexvalue_jjzjz_910883");
//        //利润月报表（总）
        listUrl.add("apply_jdgf_ygcw_report_indexvalue_lr_910298");
//        //利润月报表（一）
        listUrl.add("apply_jdgf_ygcw_report_indexvalue_lr1_910299");
        //利润月报表（二）
//        listUrl.add("apply_jdgf_ygcw_report_indexvalue_lr2_910300");
//        //利润月报表（三）
        listUrl.add("apply_jdgf_ygcw_report_indexvalue_lr3_910301");
        //煤、铝技术经济指标月报表
//        listUrl.add("apply_jdgf_ygcw_report_indexvalue_mljsjjzb_910306");
//        //企业基本情况月报表
////        listUrl.add("apply_jdgf_ygcw_report_indexvalue_qyjbqk_910307");
//        //盈利能力指标分析表
//        listUrl.add("apply_jdgf_ygcw_report_indexvalue_ylnlzb_910311");
//        //应收账款统计表
        listUrl.add("apply_jdgf_ygcw_report_indexvalue_yszktj_911973");
//        //资产负债月报表
        listUrl.add("apply_jdgf_ygcw_report_indexvalue_zcfz_910302");
//        //主要财务绩效指标分析表
        listUrl.add("apply_jdgf_ygcw_report_indexvalue_zycwjxzb_910315");
        for (int k=0;k<listUrl.size();k++) {
            int total = CwDataService.selectAllCount(listUrl.get(k));
            int page = total/100000;
            for (int j = 1; j <= page+1; j++) {
                List<CwData> cwDatas = CwDataService.selectPgaeList(j, 100000,listUrl.get(k));
                List<BasIdxDataMonth> basIdxDataMonthList = new ArrayList<>();
                for (CwData cwData : cwDatas) {
                    String sReportName = cwData.getsReportName();
                    BasIdxDataMonth basIdxDataMonth = new BasIdxDataMonth();
                    Date date2 = cwData.getDataDate();
                    //获取组织id
                    String orgStatementName = cwData.getOrgName();
                    if("江西中电投新能源发电有限公司（母公司）".equals(orgStatementName)){
                        orgStatementName = "江西中电投新能源发电有限公司（合并）";
                    }
                    if("北京吉能新能源科技有限公司".equals(orgStatementName)){
                        orgStatementName = "北京吉能新能源科技有限公司（合并）";
                    }
                    if("安徽吉电新能源有限公司（母公司）".equals(orgStatementName)){
                        orgStatementName = "安徽吉电新能源有限公司（合并）";
                    }
                    if("通化吉电智慧能源有限公司（合并）".equals(orgStatementName)){
                        orgStatementName = "通化吉电智慧能源有限公司";
                    }
                    if("磐石吉电宏日智慧能源有限公司（合并）".equals(orgStatementName)){
                        orgStatementName = "磐石吉电宏日智慧能源有限公司";
                    }
                    if("长春吉电热力有限公司（合并）".equals(orgStatementName)){
                        orgStatementName = "长春吉电热力有限公司";
                    }
                    if("吉林省富邦能源科技集团有限公司（合并）".equals(orgStatementName)){
                        orgStatementName = "吉林省富邦能源科技集团有限公司";
                    }
                    if("大安吉电绿氢能源有限公司".equals(orgStatementName)){
                        orgStatementName = "大安吉电绿氢有限公司";
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

                    String tIndexName =cwData.gettIndexName().trim();
                    if (tIndexName==null){
                        continue;
                    }
                    tIndexName = tIndexName.trim();
                    tIndexName = findIndex(tIndexName,cwData.getsIndexName());
                    String industryName = cwData.getIndustryName();
                    if ("太阳能".equals(industryName)){
                        industryName = "光伏";
                    }
                    if ("电力".equals(industryName)){
                        industryName = "汇总";
                    }
                    if ("煤电".equals(industryName)){
                        industryName = "火电";
                    }
                    if ("其他用电".equals(industryName)){
                        industryName = "其他";
                    }
                    if ("固定成本".equals(tIndexName)&&"金融服务".equals(industryName)){
                        industryName = "汇总";
                    }
                    if ("净利润".equals(tIndexName)&&"经济增加值（EVA）计算表（季报表）".equals(sReportName)){
                        continue;
                    }
                    Long basIndexId = basIndexService.selectByIndexName(tIndexName);
                    if (basIndexId==null){
                        continue;
                    }
                    LoggerFactory.getLogger("指标数据").info("指标："+tIndexName+",指标ID:"+basIndexId);
                    //获取指标值
                    BigDecimal indexValue = cwData.getIndexValue();
                    if (indexValue.compareTo(BigDecimal.ZERO)==0){
                        continue;
                    }
                    //time_type_code获取
                    String timeType = cwData.getTimeTypeCode();
                    //value_type_code获取
                    String valueType = cwData.getValueTypeCode();
                    Long property = dateProperty(timeType, valueType,basIdxDataMonth,indexValue,sReportName);
                    if (property==null){
                        continue;
                    }
                    LoggerFactory.getLogger("时间类型").info("时间类型："+"timeType:"+timeType+",valueType:"+valueType+",property:"+property);
                    Long industryNum =  basIndexService.selectBasIndustry(industryName);
                    if (industryNum==7){
                        continue;
                    }
                    //录入月数据
                    basIdxDataMonth.setSysOrg(sysOrg.getId());
                    basIdxDataMonth.setBasIndustry(industryNum);
                    basIdxDataMonth.setProject(-1L);
                    basIdxDataMonth.setDataDate(new SimpleDateFormat("yyyy-MM").format(date2)+"");
                    basIdxDataMonth.setBasIndex(basIndexId);
                    basIdxDataMonth.setDataSource(1L);
                    if (basIdxDataMonth.getDataYear()!=null
                            ||basIdxDataMonth.getDataYearPlan()!=null
                            ||basIdxDataMonth.getDataYearInit()!=null
                            ||basIdxDataMonth.getDataMonth()!=null
                            ||basIdxDataMonth.getDataMonthPlan()!=null
                    ) {
                        //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                        basIdxDataMonth.setDataInterface(sReportName);
//                        basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth,sReportName);
                        basIdxDataMonthList.add(basIdxDataMonth);
                    }

                }
                if (basIdxDataMonthList.size()==0){
                    continue;
                }
                basIdxDataMonthMapper.insertByList(basIdxDataMonthList);
            }
        }

    }

    public void dataJykjJhFDLtest() {

    }

    private void processingJykjData(JSONArray datas) {
        String sReportName = "Jykj统计";
        List<BasIdxDataMonth> basIdxDataMonthList = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            JSONObject dataJson = datas.getJSONObject(i);
            BasIdxDataMonth basIdxDataMonth = new BasIdxDataMonth();
            //获取组织id
            String orgStatementName = dataJson.getString("dim1");

//            if("国核吉林核电有限公司".equals(orgStatementName)){
//                orgStatementName = "国核吉林核电有限公司";
//            }
//            if("江西中电投新能源发电有限公司（母公司）".equals(orgStatementName)){
//                orgStatementName = "江西中电投新能源发电有限公司（合并）";
//            }
//            if("北京吉能新能源科技有限公司".equals(orgStatementName)){
//                orgStatementName = "北京吉能新能源科技有限公司（合并）";
//            }
//            if("安徽吉电新能源有限公司".equals(orgStatementName)){
//                orgStatementName = "安徽吉电新能源有限公司（合并）";
//            }
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
            if (tIndexName==null){
                continue;
            }
            tIndexName = tIndexName.trim();
            if ("发电设备平均容量".equals(tIndexName)){
                tIndexName = "期末设备平均发电容量";
            }
            if ("期末发电设备容量".equals(tIndexName)){
                tIndexName = "期末设备发电容量";
            }
            if ("发电设备平均利用小时".equals(tIndexName)){
                tIndexName = "平均利用小时";
            }

            Long basIndexId = basIndexService.selectByIndexName(tIndexName);
            if (basIndexId==null){
                continue;
            }
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
                basIdxDataMonth.setDataMonth(indexValue);
            }
            if ("累计".equals(property)){
                basIdxDataMonth.setDataYear(indexValue);
            }
            basIdxDataMonth.setSysOrg(sysOrg.getId());
            basIdxDataMonth.setBasIndustry(basIndustry);
            basIdxDataMonth.setProject(-1L);
            basIdxDataMonth.setDataDate(getStrDate(dataJson.getString("dim2"),dataJson.getString("dim4")));
            basIdxDataMonth.setBasIndex(basIndexId);
            basIdxDataMonth.setDataSource(0L);
            if (basIdxDataMonth.getDataYear()!=null
                    ||basIdxDataMonth.getDataYearPlan()!=null
                    ||basIdxDataMonth.getDataYearInit()!=null
                    ||basIdxDataMonth.getDataMonth()!=null
                    ||basIdxDataMonth.getDataMonthPlan()!=null
            ) {
                //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                basIdxDataMonth.setDataInterface(sReportName);
//                        basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth,sReportName);
                basIdxDataMonthList.add(basIdxDataMonth);
            }

        }
        if (basIdxDataMonthList.size()!=0){
            basIdxDataMonthMapper.insertByList(basIdxDataMonthList);
        }

    }

    public void dataLocalJykj() {
        List<String> listUrl = new ArrayList<>();
        //成本情况月报表
//        listUrl.add("apply_jykj_jh_fdl_jdgf_info");
        //存货统计表
//        listUrl.add("apply_jykj_jh_jdgf_info");
//        //电力技术经济指标月报表
        listUrl.add("apply_jykj_tj_jdgf_info");
        for (int k=0;k<listUrl.size();k++) {
            int total = CwDataService.selectAllCountJyKjTj(listUrl.get(k));
            int page = total/100000;
            for (int j = 1; j <= page+1; j++) {
                List<JykjDataTjLocal> cwDatas = CwDataService.selectPgaeListJyKjTj(j, 100000,listUrl.get(k));
                List<BasIdxDataMonth> basIdxDataMonthList = new ArrayList<>();
                for (JykjDataTjLocal cwData : cwDatas) {

                    String sReportName = "Jykj统计";
                    BasIdxDataMonth basIdxDataMonth = new BasIdxDataMonth();
                    //获取组织id
                    String orgStatementName = cwData.getDim1();
                    if("国核吉林核电有限公司".equals(orgStatementName)){
                        orgStatementName = "国核吉林核电有限公司";
                    }
                    if("江西中电投新能源发电有限公司（母公司）".equals(orgStatementName)){
                        orgStatementName = "江西中电投新能源发电有限公司（合并）";
                    }
                    if("北京吉能新能源科技有限公司".equals(orgStatementName)){
                        orgStatementName = "北京吉能新能源科技有限公司（合并）";
                    }
                    if("安徽吉电新能源有限公司".equals(orgStatementName)){
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
                    String tIndexName =cwData.getDim8();
                    if (tIndexName==null){
                        continue;
                    }
                    tIndexName = tIndexName.trim();
                    if ("发电设备平均容量".equals(tIndexName)){
                        tIndexName = "期末设备平均发电容量";
                    }
                    if ("期末发电设备容量".equals(tIndexName)){
                        tIndexName = "期末设备发电容量";
                    }
                    if ("发电设备平均利用小时".equals(tIndexName)){
                        tIndexName = "平均利用小时";
                    }

                    Long basIndexId = basIndexService.selectByIndexName(tIndexName);
                    if (basIndexId==null){
                        continue;
                    }
                    BigDecimal indexValue = new BigDecimal(cwData.getC1Value());
                    //获取产业类型
                    String indurtyName = cwData.getDim10();
                    String region = cwData.getDim9();
                    String property = cwData.getDim6();
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
                        basIdxDataMonth.setDataMonth(indexValue);
                    }
                    if ("累计".equals(property)){
                        basIdxDataMonth.setDataYear(indexValue);
                    }
                    basIdxDataMonth.setSysOrg(sysOrg.getId());
                    basIdxDataMonth.setBasIndustry(basIndustry);
                    basIdxDataMonth.setProject(-1L);
                    basIdxDataMonth.setDataDate(getStrDate(cwData.getDim2(), cwData.getDim4()));
                    basIdxDataMonth.setBasIndex(basIndexId);
                    basIdxDataMonth.setDataSource(0L);
                    if (basIdxDataMonth.getDataYear()!=null
                            ||basIdxDataMonth.getDataYearPlan()!=null
                            ||basIdxDataMonth.getDataYearInit()!=null
                            ||basIdxDataMonth.getDataMonth()!=null
                            ||basIdxDataMonth.getDataMonthPlan()!=null
                    ) {
                        //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                        basIdxDataMonth.setDataInterface(sReportName);
//                        basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth,sReportName);
                        basIdxDataMonthList.add(basIdxDataMonth);
                    }
            }
                if (basIdxDataMonthList.size()==0){
                    continue;
                }
                basIdxDataMonthMapper.insertByList(basIdxDataMonthList);
            }
    }
    }

    public void dataLocalJykjJH() {
        List<String> listUrl = new ArrayList<>();
        //存货统计表
        listUrl.add("apply_jykj_jh_jdgf_info");
        for (int k=0;k<listUrl.size();k++) {
            int total = CwDataService.selectAllCountJyKjTj(listUrl.get(k));
            int page = total/100000;
            for (int j = 1; j <= page+1; j++) {
                List<JykjDataJh> cwDatas = CwDataService.selectPgaeListJyKjJH(j, 100000,listUrl.get(k));
                List<BasIdxDataMonth> basIdxDataMonthList = new ArrayList<>();
                for (JykjDataJh cwData : cwDatas) {

                    String sReportName = "Jykj计划";
                    BasIdxDataMonth basIdxDataMonth = new BasIdxDataMonth();
                    //获取组织id
                    String orgStatementName = cwData.getDim3();
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
                    String tIndexName =cwData.getDim4();

                    if (tIndexName==null){
                        continue;
                    }
                    tIndexName = tIndexName.trim();
                    if ("发电设备平均容量".equals(tIndexName)){
                        tIndexName = "期末设备平均发电容量";
                    }
                    if ("期末发电设备容量".equals(tIndexName)){
                        tIndexName = "期末设备发电容量";
                    }
                    Long basIndexId = basIndexService.selectByIndexName(tIndexName);
                    if (basIndexId==null){
                        continue;
                    }
                    BigDecimal indexValue = new BigDecimal(cwData.getValue());
                    //获取产业类型
                    String indurtyName = cwData.getDim10();
                    String region = cwData.getDim9();
                    String property = cwData.getDim6();
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
                        basIdxDataMonth.setDataMonth(indexValue);
                    }
                    if ("累计".equals(property)){
                        basIdxDataMonth.setDataYear(indexValue);
                    }
                    basIdxDataMonth.setSysOrg(sysOrg.getId());
                    basIdxDataMonth.setBasIndustry(basIndustry);
                    basIdxDataMonth.setProject(-1L);
                    basIdxDataMonth.setDataDate(getStrDate(cwData.getDim2(), cwData.getDim4()));
                    basIdxDataMonth.setBasIndex(basIndexId);
                    basIdxDataMonth.setDataSource(0L);
                    if (basIdxDataMonth.getDataYear()!=null
                            ||basIdxDataMonth.getDataYearPlan()!=null
                            ||basIdxDataMonth.getDataYearInit()!=null
                            ||basIdxDataMonth.getDataMonth()!=null
                            ||basIdxDataMonth.getDataMonthPlan()!=null
                    ) {
                        //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                        basIdxDataMonth.setDataInterface(sReportName);
//                        basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth,sReportName);
                        basIdxDataMonthList.add(basIdxDataMonth);
                    }
                }
                if (basIdxDataMonthList.size()==0){
                    continue;
                }
                basIdxDataMonthMapper.insertByList(basIdxDataMonthList);
            }
        }
    }

    public void dataLocalJykjFdlJH() {
        List<String> listUrl = new ArrayList<>();
        //成本情况月报表
        listUrl.add("apply_jykj_jh_fdl_jdgf_info");
        //存货统计表
//        listUrl.add("apply_jykj_jh_jdgf_info");
//        //电力技术经济指标月报表
//        listUrl.add("apply_jykj_tj_jdgf_info");
        for (int k=0;k<listUrl.size();k++) {
            int total = CwDataService.selectAllCountJyKjTj(listUrl.get(k));
            int page = total/100000;
            for (int j = 1; j <= page+1; j++) {
                List<JykjFdlJh> cwDatas = CwDataService.selectPgaeListJyKjFdl(j, 100000,listUrl.get(k));
                List<BasIdxDataMonth> basIdxDataMonthList = new ArrayList<>();
                for (JykjFdlJh cwData : cwDatas) {

                    String sReportName = "JykjFdl计划";
                    BasIdxDataMonth basIdxDataMonth = new BasIdxDataMonth();
                    //获取组织id
                    String orgStatementName = cwData.getDim6();
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
                    String tIndexName =cwData.getDim5();
                    if (tIndexName==null){
                        continue;
                    }
                    Long basIndexId = basIndexService.selectByIndexName(tIndexName);
                    if (basIndexId==null){
                        continue;
                    }
                    BigDecimal indexValue = new BigDecimal(cwData.getValue());
                    //获取产业类型
                    String indurtyName = cwData.getDim7();
                    if ("电力合计".equals(indurtyName)){
                        indurtyName = "汇总";
                    }
                    if ("太阳能合计".equals(indurtyName)){
                        indurtyName = "太阳能";
                    }
                    if ("新能源合计".equals(indurtyName)){
                        indurtyName = "新能源";
                    }
                    if ("风电合计".equals(indurtyName)){
                        indurtyName = "风电";
                    }
                    if ("火电合计".equals(indurtyName)){
                        indurtyName = "火电";
                    }
                    if ("煤电".equals(indurtyName)){
                        continue;
                    }
                    Long basIndustry = basIndexService.selectBasIndustry(indurtyName);
                    basIdxDataMonth.setDataMonthPlan(indexValue);
                    basIdxDataMonth.setSysOrg(sysOrg.getId());
                    basIdxDataMonth.setBasIndustry(basIndustry);
                    basIdxDataMonth.setProject(-1L);
                    basIdxDataMonth.setDataDate(getStrDate(cwData.getDim2(), cwData.getDim8()));
                    basIdxDataMonth.setBasIndex(basIndexId);
                    basIdxDataMonth.setDataSource(0L);
                    if (basIdxDataMonth.getDataYear()!=null
                            ||basIdxDataMonth.getDataYearPlan()!=null
                            ||basIdxDataMonth.getDataYearInit()!=null
                            ||basIdxDataMonth.getDataMonth()!=null
                            ||basIdxDataMonth.getDataMonthPlan()!=null
                    ) {
                        //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                        basIdxDataMonth.setDataInterface(sReportName);
//                        basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth,sReportName);
                        basIdxDataMonthList.add(basIdxDataMonth);
                    }
                }
                if (basIdxDataMonthList.size()==0){
                    continue;
                }
                basIdxDataMonthMapper.insertByListFdl(basIdxDataMonthList);
            }
        }
    }

    public void dataDockJdjf() {
        List<String> listUrl = new ArrayList<>();
        listUrl.add(YSZKTJ);
        listUrl.add(ZYCWJXZB);
//        listUrl.add(JJZJZ);
        listUrl.add(ZCFZ);
//        listUrl.add(QYJBQK);
        listUrl.add(CHTJ);
//        listUrl.add(MLJSJJZB);
        listUrl.add(LR);
        listUrl.add(LRONE);
//        listUrl.add(YLNLZBX);
        listUrl.add(CBQK);
        listUrl.add(DLJSJJZB);
        listUrl.add(LRTHREE);
        listUrl.add(DXFZQK);
//        ExecutorService executorServiceDataDock = new ThreadPoolExecutor(10, 10, 0,
//                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
//        for (int i = 0; i < listUrl.size(); i++) {
//            System.out.println("启动："+listUrl.get(i)+"---第："+i);
//            executorServiceDataDock.execute(new DataDockTread(i,listUrl,URL));//填充-基本参数
//        }
//        executorServiceDataDock.shutdown();//执行线程
//        while (true) {
//            if (executorServiceDataDock.isTerminated()) {//线程池执行完毕
//                break;
//            }
//        }


        for (int i = 0; i < listUrl.size(); i++) {
            String url;
            url = URL + listUrl.get(i);
            Integer total = null;

                total = 100;

            LoggerFactory.getLogger(total.toString()).info("total:" + total);
                String urlPage = url + "&page=" + 1 + "&size=1000";
                LoggerFactory.getLogger("访问链接").info("url:" + url);
                LoggerFactory.getLogger("访问情况").info("msg:success");
        }
//        LoggerFactory.getLogger("访问链接").info("url:" + "http://10.80.140.131/iuap-data-datafusion/openapi/asset/apply_jykj_tj_jdgf_info?token=QUtNdU9RLTJRc2ctQ3YzczNPQTRLTHFHajhhQm5XdGFqS1BMTGxDcDFuUFNSMEZVaHFENHVvVndyXzZyeHZOaA==");
//        LoggerFactory.getLogger("访问情况").info("msg:success");
//
//        LoggerFactory.getLogger("访问链接").info("url:" + "http://10.80.140.131/iuap-data-datafusion/openapi/asset/apply_jykj_jh_fdl_jdgf_info?token=gyrOTuvvN-kbOYdB2Yc1XwgjCJUkPTzjtdhb_q147zjTUG5R0Pjp620_N2Gv45hA");
//        LoggerFactory.getLogger("访问情况").info("msg:success");
    }

    public void dataDockJykjtj() throws IOException {
        Integer total = sendGetTotal(JYKJ_COUNT_URL);
        LoggerFactory.getLogger(total.toString()).info("total:"+total);
        for (int j = 0; j <= (total/1000) ; j++) {
            String urlPage = JYKJ_COUNT_URL+"&page="+j+"&size=1000";
            JSONArray datas = sendGet(urlPage);
            LoggerFactory.getLogger(urlPage).info("url:"+JYKJ_COUNT_URL);
            processingJykjData(datas);
        }
    }

    public void dataDockJykjJhFdl() throws IOException {
        Integer total = sendGetTotal(JYKJ_JH_FDL);
        LoggerFactory.getLogger(total.toString()).info("total:"+total);
        for (int j = 0; j <= (total/1000) ; j++) {
            String urlPage = JYKJ_JH_FDL+"&page="+j+"&size=1000";
            JSONArray datas = sendGet(urlPage);
            LoggerFactory.getLogger(urlPage).info("url:"+JYKJ_JH_FDL);
            processingJykjJhFdlData(datas);
        }
    }


    private class DataDockTread extends Thread {
        int i;
        List<String> listUrl;
        String URL;

        public DataDockTread(int i, List<String> listUrl, String URL) {
            this.i = i;
            this.listUrl = listUrl;
            this.URL = URL;
        }

        public void run() {
            String url;
            url = URL + listUrl.get(i);
            Integer total = null;
            try {
                total = sendGetTotal(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            LoggerFactory.getLogger(total.toString()).info("total:" + total);
            int t = total / 50;
            for (int j = 0; j <= total / 100; j++) {
                String urlPage = url + "&page=" + j + "&size=100";
                JSONArray datas = null;
                try {
                    datas = sendGet(urlPage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LoggerFactory.getLogger(urlPage).info("url:" + url);
                try {
                    processingData(datas);
                } catch (InterruptedException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private class DataDockLocalTread extends Thread {
        int old;
        int nw;

        public DataDockLocalTread(int old,int nw) {
            this.old = old;
            this.nw = nw;
        }

        public void run() {
            for (int j = old; j <= nw; j++) {
                List<CwData> cwDatas = CwDataService.selectPgaeList(j, 100,"null");
                for (int i = 0; i < cwDatas.size(); i++) {
                    CwData cwData = cwDatas.get(i);
                    BasIdxDataMonth basIdxDataMonth = new BasIdxDataMonth();
                    BasIdxData basIdxData = new BasIdxData();
                    //获取组织id
                    String orgStatementName = cwData.getOrgName();
                    SysOrg sysOrg = new SysOrg();
//            sysOrg = sysOrgService.selectByDataDockCode(dataDockCode);
                    if (null == sysOrg.getId()) {
                        sysOrg = sysOrgService.selectByStatementName(orgStatementName);
                        if (null==sysOrg) {
                            continue;
                        }
                    }
                    //获取指标id
                    String tIndexName = cwData.gettIndexName();
                    if("期末装机容量".equals(tIndexName)){
                        tIndexName = "期末设备发电容量";
                    }
                    String industryName = cwData.getIndustryName()==null?"": cwData.getIndustryName();
//            if ("太阳能".equals(industryName)){
//                industryName = "光伏";
//            }
//            if ("电力".equals(industryName)){
//                industryName = "电力配套";
//            }
                    Long basIndexId = basIndexService.selectByIndexName(tIndexName);
                    LoggerFactory.getLogger("指标数据").info("指标："+tIndexName+",指标ID:"+basIndexId);
                    //获取指标值
                    BigDecimal indexValue =cwData.getIndexValue();
                    //获取时间
                    Date date = cwData.getDataDate();
                    //time_type_code获取
                    String timeType = cwData.getTimeTypeCode();
                    //value_type_code获取
                    String valueType = cwData.getValueTypeCode();
                    Long property = dateProperty(timeType, valueType,basIdxDataMonth,indexValue,null);
                    LoggerFactory.getLogger("时间类型").info("时间类型："+"timeType:"+timeType+",valueType:"+valueType+",property:"+property);
                    basIdxData.setBasIndustry(basIndexService.selectBasIndustry(industryName));
                    basIdxData.setProperty(property);
                    basIdxData.setSysOrg(sysOrg.getId());
                    basIdxData.setBasIndex(basIndexId);
                    basIdxData.setDataValue(indexValue);
                    //数据来源财务报表
                    basIdxData.setDataSource((byte)1);
                    basIdxData.setDataDate(date);
                    basIdxData.setProject(-1L);
                    if (basIdxData.getBasIndex() == null || basIdxData.getProperty() == null) {
                        continue;
                    }
                    //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                    basIdxDataService.insertBasIdxData(basIdxData,"1");
                    //录入月数据
                    basIdxDataMonth.setSysOrg(sysOrg.getId());
                    basIdxDataMonth.setBasIndustry(basIdxData.getBasIndustry());
                    basIdxDataMonth.setProject(basIdxData.getProject());
                    basIdxDataMonth.setDataDate(new SimpleDateFormat("yyyy-MM").format(date)+"");
                    basIdxDataMonth.setBasIndex(basIdxData.getBasIndex());
                    basIdxDataMonth.setDataSource(1L);
                    if (basIdxDataMonth.getDataYear()!=null
                            ||basIdxDataMonth.getDataYearPlan()!=null
                            ||basIdxDataMonth.getDataYearInit()!=null
                            ||basIdxDataMonth.getDataMonth()!=null
                            ||basIdxDataMonth.getDataMonthPlan()!=null
                    ) {
                        //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                        basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth,"1");
                    }
                }
            }
        }

    }

    public static String getStrDate(String dim2,String dim4){
        dim2 = dim2.replace("年","-");
        dim4 = dim4.replace("月","");
        int i = Integer.parseInt(dim4);
        if (i>=10) {
            return dim2+dim4;
        }
        return dim2+"0"+dim4;
    }

    public void dataDockJykjJhtest() throws IOException {
            String url2;
            String url3;
            url2 = JYKJ_PLAN_URL;

            Integer total = sendGetTotal(url2);
            LoggerFactory.getLogger(total.toString()).info("total:"+total);
            for (int j = 0; j <= total/100 ; j++) {
                String urlPage = url2+"&page="+j+"&size=100";
                JSONArray datas = sendGet(urlPage);
                LoggerFactory.getLogger(urlPage).info("url:"+url2);
                processingJykjJhData(datas);
            }

        url3 = JYKJ_JH_FDL;
        Integer total2 = sendGetTotal(url3);
        LoggerFactory.getLogger(total2.toString()).info("total:"+total2);
        for (int j = 0; j <= total2/100 ; j++) {
            String urlPage = url3+"&page="+j+"&size=100";
            JSONArray datas = sendGet(urlPage);
            LoggerFactory.getLogger(urlPage).info("url:"+url3);
            processingJykjJhFdlData(datas);
        }
    }

    public static Date getDateOne(String dim2,String dim4){
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

    private void processingJykjJhFdlData(JSONArray datas) {
        List<BasIdxDataMonth> basIdxDataMonthList = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            String sReportName = "JykjFdl计划";
            BasIdxDataMonth basIdxDataMonth = new BasIdxDataMonth();
            JSONObject dataJson = datas.getJSONObject(i);
            //获取组织id
            String orgStatementName = dataJson.getString("dim6");
            //获取组织id
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
            String tIndexName = dataJson.getString("dim5");
            if (tIndexName==null){
                continue;
            }
            Long basIndexId = basIndexService.selectByIndexName(tIndexName);
            if (basIndexId==null){
                continue;
            }
            LoggerFactory.getLogger("指标数据").info("指标："+tIndexName+",指标ID:"+basIndexId);
            //获取指标值
            BigDecimal indexValue = dataJson.getBigDecimal("value");
            //获取产业类型
            String indurtyName =  dataJson.getString("dim7");
            if ("电力合计".equals(indurtyName)){
                indurtyName = "汇总";
            }
            if ("太阳能合计".equals(indurtyName)){
                indurtyName = "太阳能";
            }
            if ("新能源合计".equals(indurtyName)){
                indurtyName = "新能源";
            }
            if ("风电合计".equals(indurtyName)){
                indurtyName = "风电";
            }
            if ("火电合计".equals(indurtyName)){
                indurtyName = "火电";
            }
            if ("煤电".equals(indurtyName)){
                continue;
            }
            Long basIndustry = basIndexService.selectBasIndustry(indurtyName);

            basIdxDataMonth.setDataDate(getStrDate(dataJson.getString("dim2"),dataJson.getString("dim8")));
            //录入月数据
            basIdxDataMonth.setDataMonthPlan(indexValue);
            basIdxDataMonth.setSysOrg(sysOrg.getId());
            basIdxDataMonth.setBasIndustry(basIndustry);
            basIdxDataMonth.setProject(-1L);
            basIdxDataMonth.setDataDate(getStrDate(dataJson.getString("dim2"),dataJson.getString("dim8")));
            basIdxDataMonth.setBasIndex(basIndexId);
            basIdxDataMonth.setDataSource(0L);
            if (basIdxDataMonth.getDataYear()!=null
                    ||basIdxDataMonth.getDataYearPlan()!=null
                    ||basIdxDataMonth.getDataYearInit()!=null
                    ||basIdxDataMonth.getDataMonth()!=null
                    ||basIdxDataMonth.getDataMonthPlan()!=null
            ) {
                //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                basIdxDataMonth.setDataInterface(sReportName);
//                        basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth,sReportName);
                basIdxDataMonthList.add(basIdxDataMonth);
            }
        }
        if (basIdxDataMonthList.size()!=0){
            basIdxDataMonthMapper.insertByListFdl(basIdxDataMonthList);
        }
    }

    /**
     * 获取jykj计划数据
     * @throws IOException
     */
    //    @Scheduled(cron = "0 0 0 15 * ?")
    public void dataDockJykjJh() throws IOException {
        /** 定时任务的名称作为key **/
        String key = "DataDockJykjJh";
        /** 设置随机key **/
        String value = UUID.randomUUID().toString().replace("-", "");
        //如果键不存在则新增,存在则不改变已经有的值。(备注：失效时间要大于多台服务器之间的时间差，如果多台服务器时间差大于超时时间，定时任务可能会执行多次)
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, value, 60, TimeUnit.SECONDS);
        if (flag != null && flag) {
            System.out.println("{"+key+"} 锁定成功，开始处理业务");
            String url;
            url = JYKJ_PLAN_URL;
            Integer total = sendGetTotal(url);
            LoggerFactory.getLogger(total.toString()).info("total:"+total);
            for (int j = 0; j <= total/100 ; j++) {
                String urlPage = url+"&page="+j+"&size=100";
                JSONArray datas = sendGet(urlPage);
                LoggerFactory.getLogger(urlPage).info("url:"+url);
                processingJykjJhData(datas);
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

    /**
     * 处理jykj计划数据
     * @param datas
     */
    private void processingJykjJhData(JSONArray datas) {
        for (int i = 0; i < datas.size(); i++) {


//            JSONObject dataJson = datas.getJSONObject(i);
//            //获取组织id
//            String dim1 = dataJson.getString("dim1")==null?"":dataJson.getString("dim1");
//            String dim10 = dataJson.getString("dim10")==null?"":dataJson.getString("dim10");
//            String dim11 = dataJson.getString("dim11")==null?"":dataJson.getString("dim11");
//            String dim2 = dataJson.getString("dim2")==null?"":dataJson.getString("dim2");
//            String dim3 = dataJson.getString("dim3")==null?"":dataJson.getString("dim3");
//            String dim4 = dataJson.getString("dim4")==null?"":dataJson.getString("dim4");
//            String dim5 = dataJson.getString("dim5")==null?"":dataJson.getString("dim5");
//            String dim6 = dataJson.getString("dim6")==null?"":dataJson.getString("dim6");
//            String dim7 = dataJson.getString("dim7")==null?"":dataJson.getString("dim7");
//            String dim8 = dataJson.getString("dim8")==null?"":dataJson.getString("dim8");
//            String dim9 = dataJson.getString("dim9")==null?"":dataJson.getString("dim9");
//            Date insertdate = dataJson.getDate("insertdate");
//            String njhztz = dataJson.getString("njhztz")==null?"":dataJson.getString("njhztz");
//            String scene = dataJson.getString("scene")==null?"":dataJson.getString("scene");
//            String value = dataJson.getString("value")==null?"":dataJson.getString("value");
//            String xmjhztz = dataJson.getString("xmjhztz")==null?"":dataJson.getString("xmjhztz");
//            JykjDataJh jykjDataJh = new JykjDataJh(dim1,dim10,dim11,
//                    dim2,dim3,dim4 ,dim5,dim6,dim7,dim8,dim9,insertdate,njhztz,
//                    scene,value,xmjhztz);
//            basIdxDataService.insertJykjJh(jykjDataJh);



            JSONObject dataJson = datas.getJSONObject(i);
            BasIdxData basIdxData = new BasIdxData();
            //获取组织id
            String orgStatementName = dataJson.getString("zuzhi");
            SysOrg sysOrg = sysOrgService.selectByStatementName(orgStatementName);
            if (null == sysOrg) {
                continue;
            }
            //获取指标id
            String tIndexName = dataJson.getString("zhibiao");
            Long basIndexId = basIndexService.selectByIndexName(tIndexName);
            LoggerFactory.getLogger("指标数据").info("指标："+tIndexName+",指标ID:"+basIndexId);
            //获取指标值
            BigDecimal indexValue = dataJson.getBigDecimal("value");
            //获取产业类型
            Long basIndustry = basIndexService.selectBasIndustry(dataJson.getString("chanyeleixing"));
            //获取时间
            basIdxData.setBasIndustry(basIndustry);
            basIdxData.setProperty(6L);
            basIdxData.setSysOrg(sysOrg.getId());
            basIdxData.setBasIndex(basIndexId);
            basIdxData.setDataValue(indexValue);
            basIdxData.setDataSource((byte)18);
            basIdxData.setDataDate(getDate(dataJson.getString("nian")));
            basIdxData.setProject(-1L);
            if (basIdxData.getBasIndex() == null || basIdxData.getProperty() == null|| basIdxData.getBasIndustry() == null) {
                continue;
            }
            basIdxDataService.insertBasIdxData(basIdxData,"2");
        }
    }


    /**
     * 处理财务报表全量数据数据
     * cron 一共可以有7个参数 以空格分开 其中年不是必须参数
     * [秒] [分] [小时] [日] [月] [周] [年]
     * 一下表示
     */
//    public void dataDockJdjfQl() throws IOException, InterruptedException {
//        List<String> listUrl = new ArrayList<>();
//        listUrl.add(YSZKTJ);
//        listUrl.add(ZYCWJXZB);
//        listUrl.add(JJZJZ);
//        listUrl.add(ZCFZ);
//        listUrl.add(QYJBQK);
//        listUrl.add(CHTJ);
//        listUrl.add(MLJSJJZB);
//        listUrl.add(LR);
//        listUrl.add(YLNLZBX);
//        listUrl.add(CBQK);
//        listUrl.add(DLJSJJZB);
//        for (int i = 0; i < listUrl.size(); i++) {
//            String url;
//            url = URL+listUrl.get(i);
//            Integer total = sendGetTotal(url);
//            LoggerFactory.getLogger(total.toString()).info("total:"+total);
//            int t = total/50;
//            for (int j = 0; j <= total/100 ; j++) {
//                String urlPage = url+"&page="+j+"&size=100";
//                JSONArray datas = sendGet(urlPage);
//                LoggerFactory.getLogger(urlPage).info("url:"+url);
//                processingData(datas);
//            }
//        }
//
//    }

    /**
     * 财务报表增量数据
     * @throws IOException
     * @throws InterruptedException
     */
//     @Scheduled(cron = "0 0 0 15 * ?")
    public void dataDockJdjfMf() throws IOException, InterruptedException, ParseException {
        /** 定时任务的名称作为key **/
        String key = "DataDockJdjfMfJob";
        /** 设置随机key **/
        String value = UUID.randomUUID().toString().replace("-", "");
        //如果键不存在则新增,存在则不改变已经有的值。(备注：失效时间要大于多台服务器之间的时间差，如果多台服务器时间差大于超时时间，定时任务可能会执行多次)
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, value, 60, TimeUnit.SECONDS);
        if (flag != null && flag) {
            System.out.println("{"+key+"} 锁定成功，开始处理业务");
            List<String> listUrl = new ArrayList<>();
            listUrl.add(YSZKTJ_MF);
            listUrl.add(ZYCWJXZB_MF);
            listUrl.add(JJZJZ_MF);
            listUrl.add(ZCFZ_MF);
//            listUrl.add(QYJBQK_MF);
            listUrl.add(CHTJ_MF);
            listUrl.add(MLJSJJZB_MF);
            listUrl.add(LR_MF);
            listUrl.add(YLNLZBX_MF);
            listUrl.add(CBQK_MF);
            listUrl.add(DLJSJJZB_MF);
            for (int i = 0; i < listUrl.size(); i++) {
                String url;
                url = URL+listUrl.get(i);
                Integer total = sendGetTotal(url);
                LoggerFactory.getLogger(total.toString()).info("total:"+total);
                for (int j = 0; j <= total/100 ; j++) {
                    String urlPage = url+"&page="+j+"&size=100";
                    JSONArray datas = sendGet(urlPage);
                    LoggerFactory.getLogger(urlPage).info("url:"+url);
                    processingData(datas);
                    }
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



    /**
     * 处理财务表表返回数据
     * @param datas
     * @throws InterruptedException
     */

    public void processingData(JSONArray datas) throws InterruptedException, ParseException {
        List<BasIdxDataMonth> basIdxDataMonthList = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            BasIdxDataMonth basIdxDataMonth = new BasIdxDataMonth();
            BasIdxData basIdxData = new BasIdxData();
            JSONObject dataJson = datas.getJSONObject(i);
//            //获取时间
            Date date = dataJson.getDate("data_date");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            String format = sdf.format(new Date());
            Date date1 = sdf.parse(format);
            if(date.compareTo(date1)>0){
                continue;
            }
            //获取组织id
            String orgStatementName = dataJson.getString("org_name");
//            if("江西中电投新能源发电有限公司（母公司）".equals(orgStatementName)){
//                orgStatementName = "江西中电投新能源发电有限公司（合并）";
//            }
//            if("北京吉能新能源科技有限公司".equals(orgStatementName)){
//                orgStatementName = "北京吉能新能源科技有限公司（合并）";
//            }
//            if("安徽吉电新能源有限公司（母公司）".equals(orgStatementName)){
//                orgStatementName = "安徽吉电新能源有限公司（合并）";
//            }
            if("通化吉电智慧能源有限公司（合并）".equals(orgStatementName)){
                orgStatementName = "通化吉电智慧能源有限公司";
            }
//            if("磐石吉电宏日智慧能源有限公司（合并）".equals(orgStatementName)){
//                orgStatementName = "磐石吉电宏日智慧能源有限公司";
//            }
//            if("长春吉电热力有限公司（合并）".equals(orgStatementName)){
//                orgStatementName = "长春吉电热力有限公司";
//            }
            if("吉林省富邦能源科技集团有限公司（合并）".equals(orgStatementName)){
                orgStatementName = "吉林省富邦能源科技集团有限公司";
            }
            if("大安吉电绿氢能源有限公司".equals(orgStatementName)){
                orgStatementName = "大安吉电绿氢有限公司";
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
            String tIndexName =dataJson.getString("t_index_name")==null?"":dataJson.getString("t_index_name").trim();
            tIndexName = findIndex(tIndexName,dataJson.getString("s_index_name")==null?"":dataJson.getString("s_index_name"));
            String industryName = dataJson.getString("industry_name")==null?"": dataJson.getString("industry_name");
            String sReportName = dataJson.getString("s_report_name")==null?"":dataJson.getString("s_report_name");
            if ("太阳能".equals(industryName)){
                industryName = "光伏";
            }
            if ("电力".equals(industryName)){
                industryName = "汇总";
            }
            if ("煤电".equals(industryName)){
                industryName = "火电";
            }
            if ("其他用电".equals(industryName)){
                industryName = "其他";
            }
            if ("固定成本".equals(tIndexName)&&"金融服务".equals(industryName)){
                industryName = "汇总";
            }
            if ("净利润".equals(tIndexName)&&"经济增加值（EVA）计算表（季报表）".equals(sReportName)){
                continue;
            }
            Long basIndexId = basIndexService.selectByIndexName(tIndexName);
            if (basIndexId==null){
                continue;
            }
            LoggerFactory.getLogger("指标数据").info("指标："+tIndexName+",指标ID:"+basIndexId);
            //获取指标值
            BigDecimal indexValue = dataJson.getBigDecimal("index_value");
            if(indexValue==null){
                continue;
            }
            if (indexValue.compareTo(BigDecimal.ZERO)==0){
                continue;
            }
            //time_type_code获取
            String timeType = dataJson.getString("time_type_code");
            //value_type_code获取
            String valueType = dataJson.getString("value_type_code");
            Long property = dateProperty(timeType, valueType,basIdxDataMonth,indexValue,sReportName);
            if (property==null){
                continue;
            }
            LoggerFactory.getLogger("时间类型").info("时间类型："+"timeType:"+timeType+",valueType:"+valueType+",property:"+property);
            Long industryNum =  basIndexService.selectBasIndustry(industryName);
            if (industryNum==7){
                continue;
            }
            basIdxDataMonth.setSysOrg(sysOrg.getId());
            basIdxDataMonth.setBasIndustry(industryNum);
            basIdxDataMonth.setProject(-1L);
            basIdxDataMonth.setDataDate(new SimpleDateFormat("yyyy-MM").format(date)+"");
            basIdxDataMonth.setBasIndex(basIndexId);
            basIdxDataMonth.setDataSource(1L);
            if (basIdxDataMonth.getDataYear()!=null
                    ||basIdxDataMonth.getDataYearPlan()!=null
                    ||basIdxDataMonth.getDataYearInit()!=null
                    ||basIdxDataMonth.getDataMonth()!=null
                    ||basIdxDataMonth.getDataMonthPlan()!=null
            ) {
                //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                basIdxDataMonth.setDataInterface(sReportName);
//                        basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth,sReportName);
                basIdxDataMonthList.add(basIdxDataMonth);
            }

        }
        if (basIdxDataMonthList.size()!=0){
            basIdxDataMonthMapper.insertByList(basIdxDataMonthList);
        }

    }

    /**
     * 财务报表转换时间类型
     * @param timetype
     * @param valuetype
     * @param basIdxDataMonth
     * @param value
     * @return
     */
    private Long dateProperty(String timetype, String valuetype,BasIdxDataMonth basIdxDataMonth,BigDecimal value,String sReportName) {
        //日实际
        if ("2".equals(timetype) && "2".equals(valuetype)) {
            return 1L;
        }
        //日计划
        if ("2".equals(timetype) && "1".equals(valuetype)) {
            return 2L;
        }
        //月实际
        if ("5".equals(timetype) && "2".equals(valuetype)) {
            basIdxDataMonth.setDataMonth(value);
            return 3L;
        }
        //月实际
        if ("35".equals(valuetype)&&"带息负债情况表（月报表）".equals(sReportName)) {
            basIdxDataMonth.setDataMonth(value);
            return 3L;
        }
        //月计划
        if ("5".equals(timetype) && "1".equals(valuetype)) {
            basIdxDataMonth.setDataMonthPlan(value);
            return 4L;
        }
        //年累计，为年数据
        if ("3".equals(valuetype)) {
            basIdxDataMonth.setDataYear(value);
            if ("主要财务绩效指标分析表".equals(sReportName)){
                basIdxDataMonth.setDataMonth(value);
            }
            return 5L;
        }
        //年计划
        if ("8".equals(timetype) && "1".equals(valuetype)) {
            basIdxDataMonth.setDataYearPlan(value);
            return 6L;
        }
        //年期末，为年数据
        if ("32".equals(valuetype)) {
            basIdxDataMonth.setDataYear(value);
            if ("资产负债月报表".equals(sReportName)){
                basIdxDataMonth.setDataMonth(value);
            }
            return 5L;
        }
        //年初值
        if ("20".equals(valuetype)) {
            basIdxDataMonth.setDataYearInit(value);
            return 7L;
        }
        //周实际
        if ("3".equals(timetype) && "2".equals(valuetype)) {
            return 8L;
        }
        return null;
    }

    /**
     * 发送get请求获取数据
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static JSONArray  sendGet(String url) throws IOException {
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

    public static Date getDate(String dim2){
        dim2 = dim2.replace("年","");
        String strDate = dim2+"-"+"01"+"-"+"01";
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = fmt.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public void dataJdjfQl() throws IOException, InterruptedException {
        List<String> listUrl = new ArrayList<>();
        listUrl.add(YSZKTJ);
        listUrl.add(ZYCWJXZB);
        listUrl.add(ZCFZ);
        listUrl.add(CHTJ);
        listUrl.add(DXFZQK);
        listUrl.add(LR);
        listUrl.add(LRONE);
        listUrl.add(LRTHREE);
        listUrl.add(CBQK);
        listUrl.add(DLJSJJZB);
        for (int i = 0; i < listUrl.size(); i++) {
            String url;
            url = URL+listUrl.get(i);
            Integer total = sendGetTotal(url);
            LoggerFactory.getLogger(total.toString()).info("total:"+total);
            for (int j = 0; j <= total/1000 ; j++) {
                String urlPage = url+"&page="+j+"&size=1000";
                JSONArray datas = sendGet(urlPage);
                LoggerFactory.getLogger(urlPage).info("url:"+url);
                processingDataData(datas);
            }
        }
    }

    private void processingDataData(JSONArray datas) {
//        for (int i = 0; i < datas.size(); i++) {
//            JSONObject dataJson = datas.getJSONObject(i);
//            CwData cwData = new CwData();
//            //获取组织id
//            String convertRatio = dataJson.getString("convert_ratio")==null?"":dataJson.getString("convert_ratio");
//            cwData.setConvertRatio(convertRatio);
//            String dataSourceCode = dataJson.getString("data_source_code")==null?"":dataJson.getString("data_source_code");
//            cwData.setDataSourceCode(dataSourceCode);
//            String finvalueDataStatus = dataJson.getString("finvalue_data_status")==null?"":dataJson.getString("finvalue_data_status");
//            cwData.setFinvalueDataStatus(finvalueDataStatus);
//            String indexCode = dataJson.getString("index_code")==null?"":dataJson.getString("index_code");
//            cwData.setIndexCode(indexCode);
//            String indexFinvalueId = dataJson.getString("index_finvalue_id")==null?"":dataJson.getString("index_finvalue_id");
//            cwData.setIndexFinvalueId(indexFinvalueId);
//            String indexValue = dataJson.getString("index_value")==null?"":dataJson.getString("index_value");
//            cwData.setIndexValue(indexValue);
//            String orgCode = dataJson.getString("org_code")==null?"":dataJson.getString("org_code");
//            cwData.setOrgCode(orgCode);
//            String orgName = dataJson.getString("org_name")==null?"":dataJson.getString("org_name");
//            cwData.setOrgName(orgName);
//            String sysCode = dataJson.getString("sys_code")==null?"":dataJson.getString("sys_code");
//            cwData.setSysCode(sysCode);
//            String sColName = dataJson.getString("s_col_name")==null?"":dataJson.getString("s_col_name");
//            cwData.setsColName(sColName);
//            String sIndexName = dataJson.getString("s_index_name")==null?"":dataJson.getString("s_index_name");
//            cwData.setsIndexName(sIndexName);
//            String sItemUnit = dataJson.getString("s_item_unit")==null?"":dataJson.getString("s_item_unit");
//            cwData.setsItemUnit(sItemUnit);
//            String sReportName = dataJson.getString("s_report_name")==null?"":dataJson.getString("s_report_name");
//            cwData.setsReportName(sReportName);
//            String timeTypeCode = dataJson.getString("time_type_code")==null?"":dataJson.getString("time_type_code");
//            cwData.setTimeTypeCode(timeTypeCode);
//            String tIndexName = dataJson.getString("t_index_name")==null?"":dataJson.getString("t_index_name");
//            cwData.settIndexName(tIndexName);
//            Long basIndexId = basIndexService.selectByIndexName(tIndexName)==null?0:basIndexService.selectByIndexName(tIndexName);
//            cwData.setBasIndexId(basIndexId);
//            String tIndexUnit = dataJson.getString("t_index_unit")==null?"":dataJson.getString("t_index_unit");
//            cwData.settIndexUnit(tIndexUnit);
//            String tTimeYype = dataJson.getString("t_time_type")==null?"":dataJson.getString("t_time_type");
//            cwData.settTimeYype(tTimeYype);
//            String tValueType = dataJson.getString("t_value_type")==null?"":dataJson.getString("t_value_type");
//            cwData.settValueType(tValueType);
//            String valueTypeCode = dataJson.getString("value_type_code")==null?"":dataJson.getString("value_type_code");
//            cwData.setValueTypeCode(valueTypeCode);
//            String industryName = dataJson.getString("industry_name")==null?"":dataJson.getString("industry_name");
//            cwData.setIndustryName(industryName);
//            Date date = dataJson.getDate("data_date");
//            cwData.setDate(date);
//            basIdxDataService.insertBasIdxDataData(cwData);
//        }
    }

    public void dataJykjJhtest() throws IOException {
        String url;
        url = JYKJ_PLAN_URL;
        Integer total = sendGetTotal(url);
        LoggerFactory.getLogger(total.toString()).info("total:"+total);
        for (int j = 0; j <= total/100 ; j++) {
            String urlPage = url+"&page="+j+"&size=100";
            JSONArray datas = sendGet(urlPage);
            LoggerFactory.getLogger(urlPage).info("url:"+url);
            processingJykjJhDataDate(datas);
        }
    }

    private void processingJykjJhDataDate(JSONArray datas) {
        for (int i = 0; i < datas.size(); i++) {
            JSONObject dataJson = datas.getJSONObject(i);
            //获取组织id
            String dim1 = dataJson.getString("dim1")==null?"":dataJson.getString("dim1");
            String dim10 = dataJson.getString("dim10")==null?"":dataJson.getString("dim10");
            String dim11 = dataJson.getString("dim11")==null?"":dataJson.getString("dim11");
            String dim2 = dataJson.getString("dim2")==null?"":dataJson.getString("dim2");
            String dim3 = dataJson.getString("dim3")==null?"":dataJson.getString("dim3");
            String dim4 = dataJson.getString("dim4")==null?"":dataJson.getString("dim4");
            String dim5 = dataJson.getString("dim5")==null?"":dataJson.getString("dim5");
            String dim6 = dataJson.getString("dim6")==null?"":dataJson.getString("dim6");
            String dim7 = dataJson.getString("dim7")==null?"":dataJson.getString("dim7");
            String dim8 = dataJson.getString("dim8")==null?"":dataJson.getString("dim8");
            String dim9 = dataJson.getString("dim9")==null?"":dataJson.getString("dim9");
            Date insertdate = dataJson.getDate("insertdate");
            String njhztz = dataJson.getString("njhztz")==null?"":dataJson.getString("njhztz");
            String scene = dataJson.getString("scene")==null?"":dataJson.getString("scene");
            String value = dataJson.getString("value")==null?"":dataJson.getString("value");
            String xmjhztz = dataJson.getString("xmjhztz")==null?"":dataJson.getString("xmjhztz");
            JykjDataJh jykjDataJh = new JykjDataJh(dim1,dim10,dim11,
                    dim2,dim3,dim4 ,dim5,dim6,dim7,dim8,dim9,insertdate,njhztz,
                    scene,value,xmjhztz);
            basIdxDataService.insertJykjJh(jykjDataJh);
        }
    }

    public void project() {
        String url;
        url = "http://10.104.0.125/iuap-data-datafusion/openapi/asset/apply_jdgf_jykj_tj_xiangmu_info?token=bjRUd09pN0I0OThKdzFsaEVaaXhJUXEyNm91Wk41X21fN1d4aHMyYk9Razd4d0tKSURmaElRQmxVNUdzLVFIYw==";
        Integer total = null;
        try {
            total = sendGetTotal(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LoggerFactory.getLogger(total.toString()).info("total:" + total);
        for (int j = 0; j <= total/1000; j++) {
            String urlPage = url + "&page=" + j + "&size=1000";
            JSONArray datas = null;
            try {
                datas = sendGet(urlPage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            LoggerFactory.getLogger(urlPage).info("url:" + url);
            try {
                processingProjectData(datas);
            } catch (InterruptedException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void processingProjectData(JSONArray datas)throws InterruptedException, ParseException {
        List<ProInvestment> proInvestments = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            ProInvestment proInvestment = new ProInvestment();
            JSONObject dataJson = datas.getJSONObject(i);
            String dimParent = dataJson.getString("dim_parent");
            String dim1 = dataJson.getString("dim1");
            String dim5 = dataJson.getString("dim5");
            String dim2 = dataJson.getString("dim2");
            String dim4 = dataJson.getString("dim4");
            String dim7 = dataJson.getString("dim7");
            String date = dim2+dim4;
            DateFormat fmt = new SimpleDateFormat("yyyy年MM月");
            Date parse = fmt.parse(date);
            proInvestment.setSysOrg(dimParent);
            proInvestment.setProjectType(dim7);
            proInvestment.setProjectName(dim1);
            proInvestment.setDataDate(parse);
            if ("本年完成".equals(dim5)){
                BigDecimal value = dataJson.getBigDecimal("c1_value");
                proInvestment.setInvestmentYear(value);
            }
            if ("自开工累计完成".equals(dim5)){
                BigDecimal value = dataJson.getBigDecimal("c1_value");
                proInvestment.setInvestmentTotal(value);
            }
            if ((!"本年完成".equals(dim5))&&(!"自开工累计完成".equals(dim5))){
                continue;
            }
            proInvestments.add(proInvestment);
        }
        if (proInvestments.size()>0){
            proInvestmentService.insertList(proInvestments);
        }
    }


}

