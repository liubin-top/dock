package com.sinosoft.util;

import com.sinosoft.mapper.BasIdxDataMonthMapper;
import com.sinosoft.model.BasIdxData;
import com.sinosoft.model.BasIdxDataMonth;
import com.sinosoft.model.SysOrg;
import com.sinosoft.service.BasIdxDataService;
import com.sinosoft.service.BasIndexService;
import com.sinosoft.service.SysOrgService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ImportDataUtil {

    private static final Map<String, String> changeMap = new HashMap<String, String>();
    private static final Map<String, String> orgChangeMap = new HashMap<String, String>();

    static {
        changeMap.put("阜新正泰太阳能发电有限公司（合并）","阜新正泰太阳能发电有限公司");
        changeMap.put("通化吉电智慧能源有限公司（合并）","通化吉电智慧能源有限公");
        changeMap.put("磐石吉电宏日智慧能源有限公司（合并）","磐石吉电宏日智慧能源有限公司");
        changeMap.put("长春吉电热力有限公司（合并）","长春吉电热力有限公司");
        changeMap.put("兴安盟双松新能源有限公司","兴安盟吉电双松新能源有限公司");
        changeMap.put("江苏凌高新能源科技有限公司（合并）","江苏凌高新能源科技有限公司");
        changeMap.put("吉林省富邦能源科技集团有限公司（合并）","吉林省富邦能源科技集团有限公司");
        changeMap.put("安徽池能新能源发展有限公司（合并）","安徽池能新能源发展有限公司");
        changeMap.put("合肥吉昭新能源有限公司（合并）","合肥吉昭新能源有限公司");
        changeMap.put("山东吉电新能源有限公司（合并）","山东吉电新能源有限公司");
        changeMap.put("寿光恒达电力有限公司（合并）","寿光恒达电力有限公司");
        changeMap.put("常州威天新能源有限公司（合并）","常州威天新能源有限公司");
    }

    static {
        changeMap.put("资产负债率（%）", "资产负债率");
        changeMap.put("资产总计", "资产总额");
        changeMap.put("期末装机容量", "期末设备发电容量");
        changeMap.put("平均装机容量", "期末设备平均发电容量");
//        changeMap.put("外购电费用","外购电、热费");//重复
        changeMap.put("外购热费用", "外购电、热费");
        changeMap.put("营业税金及附加", "税金及附加");
//        changeMap.put("归属于母公司所有者的净利润","吉电母公司利润");//有归属于母公司所有者的净利润
        changeMap.put("营业利润率（%）", "营业利润率");
        changeMap.put("EVA值", "EVA");
//        changeMap.put("净资产收益率","ROE");//没有
        changeMap.put("主营业务收入增长率（%）", "主营业务增长率");
        changeMap.put("利润增长率（%）", "利润增长率");
        changeMap.put("存货周转率（次）", "存货周转率");
        changeMap.put("应收账款周转率（次）", "应收账款周转率");
        changeMap.put("煤电售热单价", "售热单价");
        changeMap.put("外购电费用", "外购电力费");
        changeMap.put("固定资产净值", "固定资产");
//        changeMap.put("燃料","燃料金额（库存）");//没有
//        changeMap.put("备品备件","备品备件金额");//没有
        changeMap.put("长期应收款", "一年以上应收账款");
        changeMap.put("应收新能源电价补贴", "新能源电价补贴");
        changeMap.put("总资产周转率（次）", "总资产周转率");
        changeMap.put("流动资产周转率（次）", "流动资产周转率");
        changeMap.put("应收账款增长率（%）", "应收账款增长率");
        changeMap.put("资本积累率（%）", "资本积累率");
        changeMap.put("资产增长率（%）", "资产增长率");
        changeMap.put("带息负债比率（%）", "带息负债比率");
        changeMap.put("按账龄1年以内", "1年以内应收账款金额");
        changeMap.put("按账龄1-2年", "1-2年应收账款金额");
        changeMap.put("按账龄2-3年", "2-3年应收账款金额");
        changeMap.put("按账龄3-4年", "3-4年应收账款金额");
        changeMap.put("按账龄4-5年", "4-5年应收账款金额");
        changeMap.put("按账龄5年以上", "5年以上应收账款金额");
        changeMap.put("减:坏帐准备", "坏账准备");
//        changeMap.put("所有者权益（或股东权益）","所有者权益");//无法区分先存为股东权益
        changeMap.put("所有者权益（或股东权益）", "股东权益");
        changeMap.put("发电标煤耗量", "发电耗标煤量");
        changeMap.put("备件", "备品备件");
        changeMap.put("发电供热标煤单价", "发电供热标准煤单价");
//        changeMap.put("其中：银行借款","短期借款—银行");//没有
//        changeMap.put("非银行金融机构借款","短期借款—非银行");//没有
//        changeMap.put("其中：银行借款","长期借款—银行");//没有
//        changeMap.put("非银行金融机构借款","长期借款—非银行");//没有
//        changeMap.put("一、带息流动负债合计","带息流动负债合计");//没有
//        changeMap.put("二、带息非流动负债合计","带息非流动负债合计");//没有
//        changeMap.put("权益总额","权益总额");//没有
        //红
        //原为 研发费用
        changeMap.put("研究与开发费", "研发费用");
//        changeMap.put("资产减值损失","资产减值损失");//已有
//        changeMap.put("资产处置收益（损失以“-”填列）","资产处置收益");//已有
        //原为，31、供电标准煤耗
        changeMap.put("供电标准煤耗", "供电煤耗率");
        //另外一个几口
//        changeMap.put("月电网弃风率","电网弃风率");//没有
//        changeMap.put("月电网弃风电量","电网弃风电量");
//        changeMap.put("月电网弃光率","电网弃光率");
//        changeMap.put("月电网弃光电量","电网弃光电量");

//        changeMap.put("19、期末设备发电容量","设备发电容量");//已有

        //原来为21、发电单位成本
        changeMap.put("21、发电单位成本", "发电成本");//有两个指标待确定
        changeMap.put("22、售电单位成本", "售电成本");//有两个指标
        //原为-其中:利息费用
        changeMap.put("利息支出净额", "利息费用");
        changeMap.put("折旧费", "折旧费用");
        //原为：1.净资产收益率（含少数股东权益）（%）
//        changeMap.put("净资产收益率","净资产收益率（含少数股东权益）");//需要判断s_index_name
        //原为：2.净资产收益率（不含少数股东权益）（%）
        changeMap.put("归属母公司净资产收益率", "净资产收益率（不含少数股东权益）");
        changeMap.put("1、资产总额", "资产总额");//没有 相识 平均资产总额
        changeMap.put("2、负债总额", "负债总额");//没有
        //原名流动资产合计
        changeMap.put("流动资产", "流动资产总额");
        //原为-非流动资产合计
        changeMap.put("非流动资产", "非流动资产总额");
        //原为-流动负债合计
        changeMap.put("流动负债", "流动负债总额");
        //原为-非流动负债合计
        changeMap.put("非流动负债", "非流动负债总额");
        //原名-14、热管道损失
        changeMap.put("热管道损失", "供热管道损失");

        //另一个接口
//        changeMap.put("月电网限发弃光率","电网限发弃光率");
//        changeMap.put("月电网限发电量","电网限发电量");
//        changeMap.put("月电网限发弃风量","电网限发弃风量");
//        changeMap.put("月电网限发弃光量","电网限发弃光量");


//        changeMap.put("上网电量","上网电量");//没有
        //原名为6.成本费用利润率（%）
        changeMap.put("成本费用利润率（%）", "成本费用利润率");
        //原为——7.成本费用总额占营业收入的比率（%）
        changeMap.put("成本费用总额占营业收入的比率（%）", "成本费用总额占营业收入的比率");
        //原为——6.存货增长率（%）
        changeMap.put("存货增长率（%）", "存货增长率");
        //原为——5.短期借款占全部借款的比率（%）
        changeMap.put("短期借款占全部借款的比率（%）", "短期借款占全部借款的比率");
        //原为——1.营业收入增长率（%）
        changeMap.put("营业收入增长率（%）", "营业收入增长率");
//        //原为——三、煤电成本
//        changeMap.put("生产成本","煤电成本");//需要判断s_index_name
//        //原为——五、风电成本
//        changeMap.put("生产成本","风电成本");//需要判断s_index_name
//        //原为——六、太阳能成本
//        changeMap.put("生产成本","太阳能成本");//需要判断s_index_name
//        //原为——电力总成本
//        changeMap.put("生产成本","电力总成本");//需要判断s_index_name
//        //原为——八、供热成本
//        changeMap.put("生产成本","供热成本");//需要判断s_index_name
//        //原为——4、平均利用小时
//        changeMap.put("利用小时","平均利用小时");//需要判断s_index_name
//        //原为——7、厂供电量
//        changeMap.put("供电量","厂供电量");//需要判断s_index_name
        //原为——11、购入电量
        changeMap.put("购电量", "购入电量");
        //原为——30、发电标准煤量
//        changeMap.put("发电标煤耗量","发电标准煤量");//重复
        //原为——32、发电标准煤单价
        changeMap.put("发电标煤单价", "发电标准煤单价");
        //原为——33、供热标准煤量
        changeMap.put("供热标煤耗量", "供热标准煤量");
        //原为——35、供热标准煤单价
        changeMap.put("供热标煤单价", "供热标准煤单价");
        //原为——
        changeMap.put("40、发电供热用天然气体量", "发电供热用天然气体量");//没有
        //原为——39、天然煤平均单价
        changeMap.put("天然煤平均单价", "天然气平均单价");
//        changeMap.put("在三、煤电成本下面找1、燃料费","煤电燃料费");//根据产业类型已有
//        changeMap.put("在九、供热成本下面找1、燃料费","供热燃料费");//根据产业类型已有
        //原为-其中:(1)短期借款利息支出
        changeMap.put("其中:(1)短期借款利息支出", "短期借款利息支出");
        //原为-(2)长期借款利息支出
        changeMap.put("(2)长期借款利息支出", "长期借款利息支出");
//        changeMap.put("主营业务收入增长率（%）","主营业务收入增长率");//重复
        changeMap.put(" 其中：在职职工人数", "在职职工人数");
        changeMap.put("固定资产投资额", "固定资产投资");
        changeMap.put("燃料", "存货-燃料");
        changeMap.put("材料", "存货-材料");
//        changeMap.put("工程施工","存货-工程施工");//没有
        changeMap.put("低值易耗品", "存货-低值易耗品");
        changeMap.put("预付账款", "预付账款");
    }

    public String findIndex(String tName, String sName) {
        if ("生产成本".equals(tName)) {
            if ("三、煤电成本".equals(sName)) {
                tName = "煤电成本";
            }
            if ("五、风电成本".equals(sName)) {
                tName = "风电成本";
            }
            if ("六、太阳能成本".equals(sName)) {
                tName = "太阳能成本";
            }
            if ("电力总成本".equals(sName)) {
                tName = "电力总成本";
            }
            if ("八、供热成本".equals(sName)) {
                tName = "供热成本";
            }
        }
        if ("4、平均利用小时".equals(sName)) {
            tName = "平均利用小时";
        }
        if ("7、厂供电量".equals(sName)) {
            tName = "厂供电量";
        }
        if ("1.净资产收益率（含少数股东权益）（%）".equals(sName)) {
            tName = "净资产收益率（含少数股东权益）";
        }
        String s = changeMap.get(tName);
        if (StringUtils.isBlank(s)) {
            return tName;
        }

        return s;
    }

    @Autowired
    SysOrgService sysOrgService;

    @Autowired
    BasIndexService basIndexService;

    @Autowired
    BasIdxDataService basIdxDataService;

    @Autowired
    BasIdxDataMonthMapper basIdxDataMonthMapper;


    public void importData() {
        Workbook workbook = null;
        Sheet sheet = null;
        InputStream in = null;
        ClassPathResource resource = new ClassPathResource("templates/fxone71.xlsx");
        try {
            in = resource.getInputStream();
            workbook = new XSSFWorkbook(in);
            sheet = workbook.getSheet("Sheet1");
            int lastRowNum = sheet.getLastRowNum() + 1;
            int k = 1;
            for (int i = 1; i < lastRowNum; i++) {
                BasIdxDataMonth basIdxDataMonth = new BasIdxDataMonth();
                BasIdxData basIdxData = new BasIdxData();
                String orgStatementName = sheet.getRow(i).getCell(0).getStringCellValue();
                //时间

                Date date = new SimpleDateFormat("yyyy年MM月dd日").parse(sheet.getRow(i).getCell(1).getStringCellValue());
                String date2 = new SimpleDateFormat("yyyy-MM").format(date);


                String tIndexName = sheet.getRow(i).getCell(3).getStringCellValue();
                BigDecimal indexValueYear = null;
                if ("元".equals(sheet.getRow(i).getCell(4).getStringCellValue())) {
                    indexValueYear = new BigDecimal(sheet.getRow(i).getCell(5).getNumericCellValue() / 10000 + "");
                } else {
                    indexValueYear = new BigDecimal(sheet.getRow(i).getCell(5).getNumericCellValue() + "");
                }
                BigDecimal indexValueMoth = null;
                if (sheet.getRow(i).getCell(6) != null) {
                    if ("元".equals(sheet.getRow(i).getCell(4).getStringCellValue())) {
                        indexValueMoth = new BigDecimal(sheet.getRow(i).getCell(6).getNumericCellValue() / 10000 + "");
                    } else {
                        indexValueMoth = new BigDecimal(sheet.getRow(i).getCell(6).getNumericCellValue() + "");
                    }

                }
                BigDecimal indexValueYearInit = null;
                if (sheet.getRow(i).getCell(7) != null) {
                    if ("元".equals(sheet.getRow(i).getCell(4).getStringCellValue())) {
                        indexValueYearInit = new BigDecimal(sheet.getRow(i).getCell(7).getNumericCellValue() / 10000 + "");
                    } else {
                        indexValueYearInit = new BigDecimal(sheet.getRow(i).getCell(7).getNumericCellValue() + "");
                    }

                }

                SysOrg sysOrg = new SysOrg();
                if (null == sysOrg.getId()) {
                    sysOrg = sysOrgService.selectByStatementName(orgStatementName);
                    if (null == sysOrg) {
                        sysOrg = sysOrgService.selectByOrgName(orgStatementName);
                        if (null == sysOrg) {
                            continue;
                        }
                    }
                }
                tIndexName = findIndex(tIndexName, "");
                Long basIndexId = basIndexService.selectByIndexName(tIndexName);
                LoggerFactory.getLogger("指标数据").info("指标：" + tIndexName + ",指标ID:" + basIndexId);
                //获取指标值
                basIdxData.setBasIndustry(-1L);
                basIdxData.setProperty(5L);
                basIdxData.setSysOrg(sysOrg.getId());
                basIdxData.setBasIndex(basIndexId);
                basIdxData.setDataValue(indexValueYear);
                //数据来源财务报表
                basIdxData.setDataSource((byte) 1);
                basIdxData.setDataDate(date);
                basIdxData.setProject(-1L);

                //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                basIdxDataService.insertBasIdxData(basIdxData, "1");
                if (indexValueMoth != null) {
                    basIdxData.setBasIndustry(-1L);
                    basIdxData.setProperty(3L);
                    basIdxData.setSysOrg(sysOrg.getId());
                    basIdxData.setBasIndex(basIndexId);
                    basIdxData.setDataValue(indexValueMoth);
                    //数据来源财务报表
                    basIdxData.setDataSource((byte) 1);
                    basIdxData.setDataDate(date);
                    basIdxData.setProject(-1L);

                    //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                    basIdxDataService.insertBasIdxData(basIdxData, "1");
                }
                if (indexValueYearInit != null) {
                    basIdxData.setBasIndustry(-1L);
                    basIdxData.setProperty(3L);
                    basIdxData.setSysOrg(sysOrg.getId());
                    basIdxData.setBasIndex(basIndexId);
                    basIdxData.setDataValue(indexValueYearInit);
                    //数据来源财务报表
                    basIdxData.setDataSource((byte) 1);
                    basIdxData.setDataDate(date);
                    basIdxData.setProject(-1L);

                    //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                    basIdxDataService.insertBasIdxData(basIdxData, "1");
                }
                //录入月数据
                basIdxDataMonth.setSysOrg(sysOrg.getId());
                basIdxDataMonth.setBasIndustry(basIdxData.getBasIndustry());
                basIdxDataMonth.setProject(basIdxData.getProject());
                basIdxDataMonth.setDataDate(date2);
                basIdxDataMonth.setBasIndex(basIdxData.getBasIndex());
                basIdxDataMonth.setDataSource(1L);
                basIdxDataMonth.setDataYear(indexValueYear);
                if (indexValueMoth != null) {
                    basIdxDataMonth.setDataMonth(indexValueMoth);
                }
                if (indexValueYearInit != null) {
                    basIdxDataMonth.setDataYearInit(indexValueYearInit);
                }

                //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth, "1");

            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void importDataTwo() {
        Workbook workbook = null;
        Sheet sheet = null;
        InputStream in = null;
        ClassPathResource resource = new ClassPathResource("templates/2023年-重大风险监控界面数据.xlsx");
        try {
            in = resource.getInputStream();
            workbook = new XSSFWorkbook(in);
            sheet = workbook.getSheet("发电量售电单价");
            int lastRowNum = sheet.getLastRowNum() + 1;
            int k = 1;
            for (int i = 1; i < lastRowNum; i++) {
                BasIdxDataMonth basIdxDataMonth = new BasIdxDataMonth();
                BasIdxData basIdxData = new BasIdxData();
                String tIndexName = sheet.getRow(i).getCell(3).getStringCellValue();
                BigDecimal indexValueMoth = new BigDecimal(sheet.getRow(i).getCell(5).getNumericCellValue() + "");
                BigDecimal indexValueYear = new BigDecimal(sheet.getRow(i).getCell(6).getNumericCellValue() + "");
                if ("23、售电单价".equals(tIndexName)) {
                    tIndexName = "售电单价";
                    String orgStatementName = sheet.getRow(i).getCell(0).getStringCellValue();
                    orgStatementName = findorgStatementName(orgStatementName);
                    SysOrg sysOrg = new SysOrg();
                    if (null == sysOrg.getId()) {
                        sysOrg = sysOrgService.selectByStatementName(orgStatementName);
                        if (null == sysOrg) {
                            sysOrg = sysOrgService.selectByOrgName(orgStatementName);
                            if (null == sysOrg) {
                                continue;
                            }
                        }
                    }
                    //时间
                    Long basIndexId = basIndexService.selectByIndexName(tIndexName);
                    Date date = new SimpleDateFormat("yyyy年MM月dd日").parse(sheet.getRow(i).getCell(1).getStringCellValue());
                    String date2 = new SimpleDateFormat("yyyy-MM").format(date);

                    //获取指标值
                    basIdxData.setBasIndustry(-1L);
                    basIdxData.setProperty(5L);
                    basIdxData.setSysOrg(sysOrg.getId());
                    basIdxData.setBasIndex(basIndexId);
                    basIdxData.setDataValue(indexValueYear);
                    //数据来源财务报表
                    basIdxData.setDataSource((byte) 1);
                    basIdxData.setDataDate(date);
                    basIdxData.setProject(-1L);
                    basIdxDataService.insertBasIdxData(basIdxData, "1");
                    if (!(indexValueMoth.compareTo(BigDecimal.ZERO) == 0)) {
                        basIdxData.setProperty(3L);
                        basIdxData.setDataValue(indexValueMoth);
                        basIdxDataService.insertBasIdxData(basIdxData, "1");
                    }
                    //录入月数据
                    basIdxDataMonth.setSysOrg(sysOrg.getId());
                    basIdxDataMonth.setBasIndustry(basIdxData.getBasIndustry());
                    basIdxDataMonth.setProject(basIdxData.getProject());
                    basIdxDataMonth.setDataDate(date2);
                    basIdxDataMonth.setBasIndex(basIdxData.getBasIndex());
                    basIdxDataMonth.setDataSource(1L);
                    basIdxDataMonth.setDataYear(indexValueYear);
                    if (!(indexValueMoth.compareTo(BigDecimal.ZERO) == 0)) {
                        basIdxDataMonth.setDataMonth(indexValueMoth);
                    }

                    //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                    basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth, "1");
                    for (int h = 1; h < 4; h++) {
                        if ("（3）煤电综合电价".equals(sheet.getRow(i + h).getCell(3).getStringCellValue().trim())) {
                            if (!(new BigDecimal(sheet.getRow(i + h).getCell(6).getNumericCellValue() + "").compareTo(BigDecimal.ZERO) == 0)) {
                                indexValueMoth = new BigDecimal(sheet.getRow(i + h).getCell(5).getNumericCellValue() + "");
                                indexValueYear = new BigDecimal(sheet.getRow(i + h).getCell(6).getNumericCellValue() + "");

                                basIdxData.setBasIndustry(3L);
                                basIdxData.setProperty(5L);
                                basIdxData.setSysOrg(sysOrg.getId());
                                basIdxData.setBasIndex(basIndexId);
                                basIdxData.setDataValue(indexValueYear);
                                //数据来源财务报表
                                basIdxData.setDataSource((byte) 1);
                                basIdxData.setDataDate(date);
                                basIdxData.setProject(-1L);
                                basIdxDataService.insertBasIdxData(basIdxData, "1");
                                if (!(indexValueMoth.compareTo(BigDecimal.ZERO) == 0)) {
                                    basIdxData.setProperty(3L);
                                    basIdxData.setDataValue(indexValueMoth);
                                    basIdxDataService.insertBasIdxData(basIdxData, "1");
                                }
                                //录入月数据
                                basIdxDataMonth.setSysOrg(sysOrg.getId());
                                basIdxDataMonth.setBasIndustry(basIdxData.getBasIndustry());
                                basIdxDataMonth.setProject(basIdxData.getProject());
                                basIdxDataMonth.setDataDate(date2);
                                basIdxDataMonth.setBasIndex(basIdxData.getBasIndex());
                                basIdxDataMonth.setDataSource(1L);
                                basIdxDataMonth.setDataYear(indexValueYear);
                                if (!(indexValueMoth.compareTo(BigDecimal.ZERO) == 0)) {
                                    basIdxDataMonth.setDataMonth(indexValueMoth);
                                }
                                //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                                basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth, "1");

                            }
                        }
                        if ("（6）风电综合电价".equals(sheet.getRow(i + h).getCell(3).getStringCellValue().trim())) {
                            if (!(new BigDecimal(sheet.getRow(i + h).getCell(6).getNumericCellValue() + "").compareTo(BigDecimal.ZERO) == 0)) {
                                indexValueMoth = new BigDecimal(sheet.getRow(i + h).getCell(5).getNumericCellValue() + "");
                                indexValueYear = new BigDecimal(sheet.getRow(i + h).getCell(6).getNumericCellValue() + "");
                                basIdxData.setBasIndustry(2L);
                                basIdxData.setProperty(5L);
                                basIdxData.setSysOrg(sysOrg.getId());
                                basIdxData.setBasIndex(basIndexId);
                                basIdxData.setDataValue(indexValueYear);
                                //数据来源财务报表
                                basIdxData.setDataSource((byte) 1);
                                basIdxData.setDataDate(date);
                                basIdxData.setProject(-1L);
                                basIdxDataService.insertBasIdxData(basIdxData, "1");
                                if (!(indexValueMoth.compareTo(BigDecimal.ZERO) == 0)) {
                                    basIdxData.setProperty(3L);
                                    basIdxData.setDataValue(indexValueMoth);
                                    basIdxDataService.insertBasIdxData(basIdxData, "1");
                                }
                                //录入月数据
                                basIdxDataMonth.setSysOrg(sysOrg.getId());
                                basIdxDataMonth.setBasIndustry(basIdxData.getBasIndustry());
                                basIdxDataMonth.setProject(basIdxData.getProject());
                                basIdxDataMonth.setDataDate(date2);
                                basIdxDataMonth.setBasIndex(basIdxData.getBasIndex());
                                basIdxDataMonth.setDataSource(1L);
                                basIdxDataMonth.setDataYear(indexValueYear);
                                if (!(indexValueMoth.compareTo(BigDecimal.ZERO) == 0)) {
                                    basIdxDataMonth.setDataMonth(indexValueMoth);
                                }
                                //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                                basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth, "1");

                            }
                        }
                        if ("（7）太阳能综合电价".equals(sheet.getRow(i + h).getCell(3).getStringCellValue().trim())) {
                            if (!(new BigDecimal(sheet.getRow(i + h).getCell(6).getNumericCellValue() + "").compareTo(BigDecimal.ZERO) == 0)) {
                                indexValueMoth = new BigDecimal(sheet.getRow(i + h).getCell(5).getNumericCellValue() + "");
                                indexValueYear = new BigDecimal(sheet.getRow(i + h).getCell(6).getNumericCellValue() + "");
                                basIdxData.setBasIndustry(1L);
                                basIdxData.setProperty(5L);
                                basIdxData.setSysOrg(sysOrg.getId());
                                basIdxData.setBasIndex(basIndexId);
                                basIdxData.setDataValue(indexValueYear);
                                //数据来源财务报表
                                basIdxData.setDataSource((byte) 1);
                                basIdxData.setDataDate(date);
                                basIdxData.setProject(-1L);
                                basIdxDataService.insertBasIdxData(basIdxData, "1");
                                if (!(indexValueMoth.compareTo(BigDecimal.ZERO) == 0)) {
                                    basIdxData.setProperty(3L);
                                    basIdxData.setDataValue(indexValueMoth);
                                    basIdxDataService.insertBasIdxData(basIdxData, "1");
                                }
                                //录入月数据
                                basIdxDataMonth.setSysOrg(sysOrg.getId());
                                basIdxDataMonth.setBasIndustry(basIdxData.getBasIndustry());
                                basIdxDataMonth.setProject(basIdxData.getProject());
                                basIdxDataMonth.setDataDate(date2);
                                basIdxDataMonth.setBasIndex(basIdxData.getBasIndex());
                                basIdxDataMonth.setDataSource(1L);
                                basIdxDataMonth.setDataYear(indexValueYear);
                                if (!(indexValueMoth.compareTo(BigDecimal.ZERO) == 0)) {
                                    basIdxDataMonth.setDataMonth(indexValueMoth);
                                }
                                //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                                basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth, "1");

                            }
                            break;
                        }
                    }
                    i = i + 3;

                }
                if ("3、发电量".equals(tIndexName)) {
                    tIndexName = "发电量";
                    String orgStatementName = sheet.getRow(i).getCell(0).getStringCellValue();
                    SysOrg sysOrg = new SysOrg();
                    if (null == sysOrg.getId()) {
                        sysOrg = sysOrgService.selectByStatementName(orgStatementName);
                        if (null == sysOrg) {
                            sysOrg = sysOrgService.selectByOrgName(orgStatementName);
                            if (null == sysOrg) {
                                continue;
                            }
                        }
                    }
                    //时间
                    Long basIndexId = basIndexService.selectByIndexName(tIndexName);
                    Date date = new SimpleDateFormat("yyyy年MM月dd日").parse(sheet.getRow(i).getCell(1).getStringCellValue());
                    String date2 = new SimpleDateFormat("yyyy-MM").format(date);

                    //获取指标值
                    basIdxData.setBasIndustry(-1L);
                    basIdxData.setProperty(5L);
                    basIdxData.setSysOrg(sysOrg.getId());
                    basIdxData.setBasIndex(basIndexId);
                    basIdxData.setDataValue(indexValueYear);
                    //数据来源财务报表
                    basIdxData.setDataSource((byte) 1);
                    basIdxData.setDataDate(date);
                    basIdxData.setProject(-1L);
                    basIdxDataService.insertBasIdxData(basIdxData, "1");
                    if (!(indexValueMoth.compareTo(BigDecimal.ZERO) == 0)) {
                        basIdxData.setProperty(3L);
                        basIdxData.setDataValue(indexValueMoth);
                        basIdxDataService.insertBasIdxData(basIdxData, "1");
                    }
                    //录入月数据
                    basIdxDataMonth.setSysOrg(sysOrg.getId());
                    basIdxDataMonth.setBasIndustry(basIdxData.getBasIndustry());
                    basIdxDataMonth.setProject(basIdxData.getProject());
                    basIdxDataMonth.setDataDate(date2);
                    basIdxDataMonth.setBasIndex(basIdxData.getBasIndex());
                    basIdxDataMonth.setDataSource(1L);
                    basIdxDataMonth.setDataYear(indexValueYear);
                    if (!(indexValueMoth.compareTo(BigDecimal.ZERO) == 0)) {
                        basIdxDataMonth.setDataMonth(indexValueMoth);
                    }

                    //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                    basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth, "1");
                    for (int j = 1; j < 4; j++) {
                        if ("(3)煤电".equals(sheet.getRow(i + j).getCell(3).getStringCellValue().trim())) {
                            if (!(new BigDecimal(sheet.getRow(i + j).getCell(6).getNumericCellValue() + "").compareTo(BigDecimal.ZERO) == 0)) {
                                indexValueMoth = new BigDecimal(sheet.getRow(i + j).getCell(5).getNumericCellValue() + "");
                                indexValueYear = new BigDecimal(sheet.getRow(i + j).getCell(6).getNumericCellValue() + "");

                                basIdxData.setBasIndustry(3L);
                                basIdxData.setProperty(5L);
                                basIdxData.setSysOrg(sysOrg.getId());
                                basIdxData.setBasIndex(basIndexId);
                                basIdxData.setDataValue(indexValueYear);
                                //数据来源财务报表
                                basIdxData.setDataSource((byte) 1);
                                basIdxData.setDataDate(date);
                                basIdxData.setProject(-1L);
                                basIdxDataService.insertBasIdxData(basIdxData, "1");
                                if (!(indexValueMoth.compareTo(BigDecimal.ZERO) == 0)) {
                                    basIdxData.setProperty(3L);
                                    basIdxData.setDataValue(indexValueMoth);
                                    basIdxDataService.insertBasIdxData(basIdxData, "1");
                                }
                                //录入月数据
                                basIdxDataMonth.setSysOrg(sysOrg.getId());
                                basIdxDataMonth.setBasIndustry(basIdxData.getBasIndustry());
                                basIdxDataMonth.setProject(basIdxData.getProject());
                                basIdxDataMonth.setDataDate(date2);
                                basIdxDataMonth.setBasIndex(basIdxData.getBasIndex());
                                basIdxDataMonth.setDataSource(1L);
                                basIdxDataMonth.setDataYear(indexValueYear);
                                if (!(indexValueMoth.compareTo(BigDecimal.ZERO) == 0)) {
                                    basIdxDataMonth.setDataMonth(indexValueMoth);
                                }
                                //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                                basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth, "1");

                            }
                        }
                        if ("(6)风电".equals(sheet.getRow(i + j).getCell(3).getStringCellValue().trim())) {
                            if (!(new BigDecimal(sheet.getRow(i + j).getCell(6).getNumericCellValue() + "").compareTo(BigDecimal.ZERO) == 0)) {
                                indexValueMoth = new BigDecimal(sheet.getRow(i + j).getCell(5).getNumericCellValue() + "");
                                indexValueYear = new BigDecimal(sheet.getRow(i + j).getCell(6).getNumericCellValue() + "");
                                basIdxData.setBasIndustry(2L);
                                basIdxData.setProperty(5L);
                                basIdxData.setSysOrg(sysOrg.getId());
                                basIdxData.setBasIndex(basIndexId);
                                basIdxData.setDataValue(indexValueYear);
                                //数据来源财务报表
                                basIdxData.setDataSource((byte) 1);
                                basIdxData.setDataDate(date);
                                basIdxData.setProject(-1L);
                                basIdxDataService.insertBasIdxData(basIdxData, "1");
                                if (!(indexValueMoth.compareTo(BigDecimal.ZERO) == 0)) {
                                    basIdxData.setProperty(3L);
                                    basIdxData.setDataValue(indexValueMoth);
                                    basIdxDataService.insertBasIdxData(basIdxData, "1");
                                }
                                //录入月数据
                                basIdxDataMonth.setSysOrg(sysOrg.getId());
                                basIdxDataMonth.setBasIndustry(basIdxData.getBasIndustry());
                                basIdxDataMonth.setProject(basIdxData.getProject());
                                basIdxDataMonth.setDataDate(date2);
                                basIdxDataMonth.setBasIndex(basIdxData.getBasIndex());
                                basIdxDataMonth.setDataSource(1L);
                                basIdxDataMonth.setDataYear(indexValueYear);
                                if (!(indexValueMoth.compareTo(BigDecimal.ZERO) == 0)) {
                                    basIdxDataMonth.setDataMonth(indexValueMoth);
                                }
                                //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                                basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth, "1");

                            }
                        }
                        if ("(7)太阳能".equals(sheet.getRow(i + j).getCell(3).getStringCellValue().trim())) {
                            if (!(new BigDecimal(sheet.getRow(i + j).getCell(6).getNumericCellValue() + "").compareTo(BigDecimal.ZERO) == 0)) {
                                indexValueMoth = new BigDecimal(sheet.getRow(i + j).getCell(5).getNumericCellValue() + "");
                                indexValueYear = new BigDecimal(sheet.getRow(i + j).getCell(6).getNumericCellValue() + "");
                                basIdxData.setBasIndustry(1L);
                                basIdxData.setProperty(5L);
                                basIdxData.setSysOrg(sysOrg.getId());
                                basIdxData.setBasIndex(basIndexId);
                                basIdxData.setDataValue(indexValueYear);
                                //数据来源财务报表
                                basIdxData.setDataSource((byte) 1);
                                basIdxData.setDataDate(date);
                                basIdxData.setProject(-1L);
                                basIdxDataService.insertBasIdxData(basIdxData, "1");
                                if (!(indexValueMoth.compareTo(BigDecimal.ZERO) == 0)) {
                                    basIdxData.setProperty(3L);
                                    basIdxData.setDataValue(indexValueMoth);
                                    basIdxDataService.insertBasIdxData(basIdxData, "1");
                                }
                                //录入月数据
                                basIdxDataMonth.setSysOrg(sysOrg.getId());
                                basIdxDataMonth.setBasIndustry(basIdxData.getBasIndustry());
                                basIdxDataMonth.setProject(basIdxData.getProject());
                                basIdxDataMonth.setDataDate(date2);
                                basIdxDataMonth.setBasIndex(basIdxData.getBasIndex());
                                basIdxDataMonth.setDataSource(1L);
                                basIdxDataMonth.setDataYear(indexValueYear);
                                if (!(indexValueMoth.compareTo(BigDecimal.ZERO) == 0)) {
                                    basIdxDataMonth.setDataMonth(indexValueMoth);
                                }
                                //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
                                basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth, "1");

                            }
                            break;
                        }
                    }
                    i = i + 3;
                }

            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String findorgStatementName(String orgStatementName) {
        String s = orgChangeMap.get(orgStatementName);
        if (StringUtils.isBlank(s)) {
            return orgStatementName;
        }

        return s;
    }

    public void importDataTwoOne() {
        Workbook workbook = null;
        Sheet sheet1 = null;
        Sheet sheet2 = null;
        Sheet sheet3 = null;
        Sheet sheet4 = null;
        InputStream in = null;
        ClassPathResource resource = new ClassPathResource("templates/dr918.xlsx");
        try {
            in = resource.getInputStream();
            workbook = new XSSFWorkbook(in);
            sheet1 = workbook.getSheet("利润月报表（总）（数据）");
            sheet2 = workbook.getSheet("主要财务绩效指标分析表（数据）");
            sheet3 = workbook.getSheet("资产负债月报表（数据）");
//            sheet4 = workbook.getSheet("电力技术指标表（数据）");
//            handleData(sheet1);
            handleDataYear(sheet2);
            handleDataInit(sheet3);
//            handleData(sheet4);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleData(Sheet sheet) throws ParseException {
        int lastRowNum = sheet.getLastRowNum() + 1;
        for (int i = 1; i < lastRowNum; i++) {
            BasIdxDataMonth basIdxDataMonth = new BasIdxDataMonth();
            BasIdxData basIdxData = new BasIdxData();
            String tIndexName = sheet.getRow(i).getCell(3).getStringCellValue().trim();
            String orgName = sheet.getRow(i).getCell(0).getStringCellValue();
            basIdxData.setBasIndustry(-1L);
            BigDecimal indexValueMoth =  new BigDecimal(0);
            BigDecimal indexValueYear = new BigDecimal(0);
            if (sheet.getRow(i).getCell(5)!=null){
                indexValueMoth =  new BigDecimal(sheet.getRow(i).getCell(5).getNumericCellValue() + "");
                if ("元".equals(sheet.getRow(i).getCell(4).getStringCellValue())){
                    indexValueMoth = new BigDecimal(sheet.getRow(i).getCell(5).getNumericCellValue() / 10000 + "");
                }
            }
            if (sheet.getRow(i).getCell(6)!=null){
                indexValueYear = new BigDecimal(sheet.getRow(i).getCell(6).getNumericCellValue() + "");
                if ("元".equals(sheet.getRow(i).getCell(4).getStringCellValue())) {
                    indexValueYear = new BigDecimal(sheet.getRow(i).getCell(6).getNumericCellValue() / 10000 + "");
                }
            }
            if (indexValueMoth.compareTo(BigDecimal.ZERO) == 0 && indexValueYear.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }


//                    if (indexValueYear.compareTo(BigDecimal.ZERO)==0){
//                        continue;
//                    }
            String orgStatementName = sheet.getRow(i).getCell(0).getStringCellValue();
            orgStatementName = findorgStatementName(orgStatementName);
            SysOrg sysOrg = new SysOrg();
            if (null == sysOrg.getId()) {
                sysOrg = sysOrgService.selectByStatementName(orgStatementName);
                if (null == sysOrg) {
                    sysOrg = sysOrgService.selectByOrgName(orgStatementName);
                    if (null == sysOrg) {
                        continue;
                    }
                }
            }
            //时间
            Long basIndexId = basIndexService.selectByIndexName(tIndexName);
            Date date = new SimpleDateFormat("yyyy年MM月dd日").parse(sheet.getRow(i).getCell(1).getStringCellValue());
            String date2 = new SimpleDateFormat("yyyy-MM").format(date);


            basIdxData.setSysOrg(sysOrg.getId());
            basIdxData.setBasIndex(basIndexId);

            //数据来源财务报表
            basIdxData.setDataSource((byte) 1);
            basIdxData.setDataDate(date);
            basIdxData.setProject(-1L);
            if (!(indexValueYear.compareTo(BigDecimal.ZERO) == 0)) {
                basIdxData.setProperty(5L);
                basIdxData.setDataValue(indexValueYear);
                basIdxDataService.insertBasIdxData(basIdxData, "1");
            }
            if (!(indexValueMoth.compareTo(BigDecimal.ZERO) == 0)) {
                basIdxData.setProperty(7L);
                basIdxData.setDataValue(indexValueMoth);
                basIdxDataService.insertBasIdxData(basIdxData, "1");
            }
            //录入月数据
            basIdxDataMonth.setSysOrg(sysOrg.getId());
            basIdxDataMonth.setBasIndustry(basIdxData.getBasIndustry());
            basIdxDataMonth.setProject(basIdxData.getProject());
            basIdxDataMonth.setDataDate(date2);
            basIdxDataMonth.setBasIndex(basIdxData.getBasIndex());
            basIdxDataMonth.setDataSource(1L);
            if (!(indexValueYear.compareTo(BigDecimal.ZERO) == 0)) {
                basIdxDataMonth.setDataYear(indexValueYear);
            }
            if (!(indexValueMoth.compareTo(BigDecimal.ZERO) == 0)) {
                basIdxDataMonth.setDataMonth(indexValueMoth);
            }
            //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
            basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth, "1");


        }
    }

    private void handleDataYear(Sheet sheet) throws ParseException {
        int lastRowNum = sheet.getLastRowNum() + 1;
        for (int i = 1; i < lastRowNum; i++) {
            BasIdxDataMonth basIdxDataMonth = new BasIdxDataMonth();
            BasIdxData basIdxData = new BasIdxData();
            String tIndexName = sheet.getRow(i).getCell(3).getStringCellValue().trim();
            String orgName = sheet.getRow(i).getCell(0).getStringCellValue();
            basIdxData.setBasIndustry(-1L);
            BigDecimal indexValueYear = null;
            if ("元".equals(sheet.getRow(i).getCell(4).getStringCellValue())) {
                indexValueYear = new BigDecimal(sheet.getRow(i).getCell(5).getNumericCellValue() / 10000 + "");
            } else {
                indexValueYear = new BigDecimal(sheet.getRow(i).getCell(5).getNumericCellValue() + "");
            }

            if (indexValueYear.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
//                    if (indexValueYear.compareTo(BigDecimal.ZERO)==0){
//                        continue;
//                    }
            String orgStatementName = sheet.getRow(i).getCell(0).getStringCellValue();
            orgStatementName = findorgStatementName(orgStatementName);
            SysOrg sysOrg = new SysOrg();
            if (null == sysOrg.getId()) {
                sysOrg = sysOrgService.selectByStatementName(orgStatementName);
                if (null == sysOrg) {
                    sysOrg = sysOrgService.selectByOrgName(orgStatementName);
                    if (null == sysOrg) {
                        continue;
                    }
                }
            }
            //时间
            Long basIndexId = basIndexService.selectByIndexName(tIndexName);
            Date date = new SimpleDateFormat("yyyy年MM月dd日").parse(sheet.getRow(i).getCell(1).getStringCellValue());
            String date2 = new SimpleDateFormat("yyyy-MM").format(date);


            basIdxData.setSysOrg(sysOrg.getId());
            basIdxData.setBasIndex(basIndexId);

            //数据来源财务报表
            basIdxData.setDataSource((byte) 1);
            basIdxData.setDataDate(date);
            basIdxData.setProject(-1L);
            if (!(indexValueYear.compareTo(BigDecimal.ZERO) == 0)) {
                basIdxData.setProperty(5L);
                basIdxData.setDataValue(indexValueYear);
                basIdxDataService.insertBasIdxData(basIdxData, "1");
            }
            //录入月数据
            basIdxDataMonth.setSysOrg(sysOrg.getId());
            basIdxDataMonth.setBasIndustry(basIdxData.getBasIndustry());
            basIdxDataMonth.setProject(basIdxData.getProject());
            basIdxDataMonth.setDataDate(date2);
            basIdxDataMonth.setBasIndex(basIdxData.getBasIndex());
            basIdxDataMonth.setDataSource(1L);
            if (!(indexValueYear.compareTo(BigDecimal.ZERO) == 0)) {
                basIdxDataMonth.setDataYear(indexValueYear);
            }
            //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
            basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth, "1");


        }
    }

    private void handleDataInit(Sheet sheet) throws ParseException {
        int lastRowNum = sheet.getLastRowNum() + 1;
        for (int i = 1; i < lastRowNum; i++) {
            BasIdxDataMonth basIdxDataMonth = new BasIdxDataMonth();
            BasIdxData basIdxData = new BasIdxData();
            String tIndexName = sheet.getRow(i).getCell(3).getStringCellValue().trim();
            String orgName = sheet.getRow(i).getCell(0).getStringCellValue();
            basIdxData.setBasIndustry(-1L);
            BigDecimal indexValueInit =  new BigDecimal(0);
            BigDecimal indexValueYear = new BigDecimal(0);
            if (sheet.getRow(i).getCell(5)!=null){
                indexValueInit =  new BigDecimal(sheet.getRow(i).getCell(5).getNumericCellValue() + "");
                if ("元".equals(sheet.getRow(i).getCell(4).getStringCellValue())){
                    indexValueInit = new BigDecimal(sheet.getRow(i).getCell(5).getNumericCellValue() / 10000 + "");
                }
            }
            if (sheet.getRow(i).getCell(6)!=null){
                indexValueYear = new BigDecimal(sheet.getRow(i).getCell(6).getNumericCellValue() + "");
                if ("元".equals(sheet.getRow(i).getCell(4).getStringCellValue())) {
                    indexValueYear = new BigDecimal(sheet.getRow(i).getCell(6).getNumericCellValue() / 10000 + "");
                }
            }
            if (indexValueInit.compareTo(BigDecimal.ZERO) == 0 && indexValueYear.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
//                    if (indexValueYear.compareTo(BigDecimal.ZERO)==0){
//                        continue;
//                    }
            String orgStatementName = sheet.getRow(i).getCell(0).getStringCellValue();
            orgStatementName = findorgStatementName(orgStatementName);
            SysOrg sysOrg = new SysOrg();
            if (null == sysOrg.getId()) {
                sysOrg = sysOrgService.selectByStatementName(orgStatementName);
                if (null == sysOrg) {
                    sysOrg = sysOrgService.selectByOrgName(orgStatementName);
                    if (null == sysOrg) {
                        continue;
                    }
                }
            }
            //时间
            Long basIndexId = basIndexService.selectByIndexName(tIndexName);
            Date date = new SimpleDateFormat("yyyy年MM月dd日").parse(sheet.getRow(i).getCell(1).getStringCellValue());
            String date2 = new SimpleDateFormat("yyyy-MM").format(date);


            basIdxData.setSysOrg(sysOrg.getId());
            basIdxData.setBasIndex(basIndexId);

            //数据来源财务报表
            basIdxData.setDataSource((byte) 1);
            basIdxData.setDataDate(date);
            basIdxData.setProject(-1L);
            if (!(indexValueYear.compareTo(BigDecimal.ZERO) == 0)) {
                basIdxData.setProperty(5L);
                basIdxData.setDataValue(indexValueYear);
                basIdxDataService.insertBasIdxData(basIdxData, "1");
            }
            if (!(indexValueInit.compareTo(BigDecimal.ZERO) == 0)) {
                basIdxData.setProperty(7L);
                basIdxData.setDataValue(indexValueInit);
                basIdxDataService.insertBasIdxData(basIdxData, "1");
            }
            //录入月数据
            basIdxDataMonth.setSysOrg(sysOrg.getId());
            basIdxDataMonth.setBasIndustry(basIdxData.getBasIndustry());
            basIdxDataMonth.setProject(basIdxData.getProject());
            basIdxDataMonth.setDataDate(date2);
            basIdxDataMonth.setBasIndex(basIdxData.getBasIndex());
            basIdxDataMonth.setDataSource(1L);
            if (!(indexValueYear.compareTo(BigDecimal.ZERO) == 0)) {
                basIdxDataMonth.setDataYear(indexValueYear);
            }
            if (!(indexValueInit.compareTo(BigDecimal.ZERO) == 0)) {
                basIdxDataMonth.setDataYearInit(indexValueInit);
            }
            //数据来源接口:1 远光财务报表 2 jykj计划数据 3 jykj统计数据 4 吉电股份电力营销数据
            basIdxDataMonthMapper.insertMonthDate(basIdxDataMonth, "1");


        }
    }

}
