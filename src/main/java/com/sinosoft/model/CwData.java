package com.sinosoft.model;


import java.math.BigDecimal;
import java.util.Date;

public class CwData {
    private String sReportName;
    private String sIndexName;
    private String tIndexName;
    private String timeTypeCode;
    private String valueTypeCode;
    private BigDecimal indexValue;
    private String orgName;
    private String industryName;
    private Date dataDate;

    public CwData() {
    }

    public CwData(String sReportName, String sIndexName, String tIndexName, String timeTypeCode, String valueTypeCode, BigDecimal indexValue, String orgName, String industryName, Date dataDate) {
        this.sReportName = sReportName;
        this.sIndexName = sIndexName;
        this.tIndexName = tIndexName;
        this.timeTypeCode = timeTypeCode;
        this.valueTypeCode = valueTypeCode;
        this.indexValue = indexValue;
        this.orgName = orgName;
        this.industryName = industryName;
        this.dataDate = dataDate;
    }

    public String getsReportName() {
        return sReportName;
    }

    public void setsReportName(String sReportName) {
        this.sReportName = sReportName;
    }

    public String getsIndexName() {
        return sIndexName;
    }

    public void setsIndexName(String sIndexName) {
        this.sIndexName = sIndexName;
    }

    public String gettIndexName() {
        return tIndexName;
    }

    public void settIndexName(String tIndexName) {
        this.tIndexName = tIndexName;
    }

    public String getTimeTypeCode() {
        return timeTypeCode;
    }

    public void setTimeTypeCode(String timeTypeCode) {
        this.timeTypeCode = timeTypeCode;
    }

    public String getValueTypeCode() {
        return valueTypeCode;
    }

    public void setValueTypeCode(String valueTypeCode) {
        this.valueTypeCode = valueTypeCode;
    }

    public BigDecimal getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(BigDecimal indexValue) {
        this.indexValue = indexValue;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    public Date getDataDate() {
        return dataDate;
    }

    public void setDataDate(Date dataDate) {
        this.dataDate = dataDate;
    }
}
