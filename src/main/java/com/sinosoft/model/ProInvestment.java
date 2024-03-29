package com.sinosoft.model;

import java.math.BigDecimal;
import java.util.Date;

public class ProInvestment {

    private String sysOrg;

    private String projectName;

    private String projectType;

    private Date dataDate;

    private String installedCapacity;

    private BigDecimal investmentYearPlan;

    private BigDecimal investmentYear;

    private BigDecimal investmentTotalPlan;

    private BigDecimal investmentTotal;

    public ProInvestment(String sysOrg, String projectName, String projectType, Date dataDate, String installedCapacity, BigDecimal investmentYearPlan, BigDecimal investmentYear, BigDecimal investmentTotalPlan, BigDecimal investmentTotal) {
        this.sysOrg = sysOrg;
        this.projectName = projectName;
        this.projectType = projectType;
        this.dataDate = dataDate;
        this.installedCapacity = installedCapacity;
        this.investmentYearPlan = investmentYearPlan;
        this.investmentYear = investmentYear;
        this.investmentTotalPlan = investmentTotalPlan;
        this.investmentTotal = investmentTotal;
    }

    public ProInvestment() {
    }

    public String getSysOrg() {
        return sysOrg;
    }

    public void setSysOrg(String sysOrg) {
        this.sysOrg = sysOrg;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public Date getDataDate() {
        return dataDate;
    }

    public void setDataDate(Date dataDate) {
        this.dataDate = dataDate;
    }

    public String getInstalledCapacity() {
        return installedCapacity;
    }

    public void setInstalledCapacity(String installedCapacity) {
        this.installedCapacity = installedCapacity;
    }

    public BigDecimal getInvestmentYearPlan() {
        return investmentYearPlan;
    }

    public void setInvestmentYearPlan(BigDecimal investmentYearPlan) {
        this.investmentYearPlan = investmentYearPlan;
    }

    public BigDecimal getInvestmentYear() {
        return investmentYear;
    }

    public void setInvestmentYear(BigDecimal investmentYear) {
        this.investmentYear = investmentYear;
    }

    public BigDecimal getInvestmentTotalPlan() {
        return investmentTotalPlan;
    }

    public void setInvestmentTotalPlan(BigDecimal investmentTotalPlan) {
        this.investmentTotalPlan = investmentTotalPlan;
    }

    public BigDecimal getInvestmentTotal() {
        return investmentTotal;
    }

    public void setInvestmentTotal(BigDecimal investmentTotal) {
        this.investmentTotal = investmentTotal;
    }
}
