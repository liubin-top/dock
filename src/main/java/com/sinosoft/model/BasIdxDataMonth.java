package com.sinosoft.model;

import java.math.BigDecimal;

public class BasIdxDataMonth {
    private Long id;
    private	Long	sysOrg;
    private	Long	basIndustry;
    private	Long	project;
    private	String	dataDate;
    private	Long	basIndex;
    private	Long	dataSource;
    private	BigDecimal	dataMonth;
    private	BigDecimal	dataMonthPlan;
    private	BigDecimal	dataYear;
    private	BigDecimal	dataYearPlan;
    private	BigDecimal	dataYearInit;
    private String dataInterface;

    public String getDataInterface() {
        return dataInterface;
    }

    public void setDataInterface(String dataInterface) {
        this.dataInterface = dataInterface;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSysOrg() {
        return sysOrg;
    }

    public void setSysOrg(Long sysOrg) {
        this.sysOrg = sysOrg;
    }

    public Long getBasIndustry() {
        return basIndustry;
    }

    public void setBasIndustry(Long basIndustry) {
        this.basIndustry = basIndustry;
    }

    public Long getProject() {
        return project;
    }

    public void setProject(Long project) {
        this.project = project;
    }

    public String getDataDate() {
        return dataDate;
    }

    public void setDataDate(String dataDate) {
        this.dataDate = dataDate;
    }

    public Long getBasIndex() {
        return basIndex;
    }

    public void setBasIndex(Long basIndex) {
        this.basIndex = basIndex;
    }

    public Long getDataSource() {
        return dataSource;
    }

    public void setDataSource(Long dataSource) {
        this.dataSource = dataSource;
    }

    public BigDecimal getDataMonth() {
        return dataMonth;
    }

    public void setDataMonth(BigDecimal dataMonth) {
        this.dataMonth = dataMonth;
    }

    public BigDecimal getDataMonthPlan() {
        return dataMonthPlan;
    }

    public void setDataMonthPlan(BigDecimal dataMonthPlan) {
        this.dataMonthPlan = dataMonthPlan;
    }

    public BigDecimal getDataYear() {
        return dataYear;
    }

    public void setDataYear(BigDecimal dataYear) {
        this.dataYear = dataYear;
    }

    public BigDecimal getDataYearPlan() {
        return dataYearPlan;
    }

    public void setDataYearPlan(BigDecimal dataYearPlan) {
        this.dataYearPlan = dataYearPlan;
    }

    public BigDecimal getDataYearInit() {
        return dataYearInit;
    }

    public void setDataYearInit(BigDecimal dataYearInit) {
        this.dataYearInit = dataYearInit;
    }
}