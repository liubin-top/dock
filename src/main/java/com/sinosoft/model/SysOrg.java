package com.sinosoft.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SysOrg {
    private Long id;

    private Long pid;

    private String orgCode;

    private String orgSimpleName;

    private String orgName;

    private String pName;

    private String orgRemark;

    private Byte isStatistics;

    private Byte isPartner;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    private Date registerTime;

    private String registerPlace;

    private BigDecimal registerCapital;

    private Integer orderby;

    private Date createDate;

    private Date updateDate;

    private Byte isValid;

    private Date invalidDate;

    private Byte orgAttr;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8:00")
    private Date periodStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8:00")
    private Date periodEnd;

    private Long legalPerson;

    private Long industry;

    private Byte isBranchOffice;

    private String orgAttrName;//机构属性名称

    private String legalPersonName;//法人名称

    private Byte isPromise;//法人是否被列入失信名单

    private String sysOrgIndustryClassifyNames;//主营业务-行业分类名称

    private List<SysOrg> children;

    private List<BasShareholder> basShareholderList;

    private List<SysOrgShareholder> sysOrgShareholderList;

    private Long[] sysOrgIndustryClassifys;//主营业务-行业分类

    public Long getIndustry() {
        return industry;
    }

    public void setIndustry(Long industry) {
        this.industry = industry;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode == null ? null : orgCode.trim();
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName == null ? null : orgName.trim();
    }

    public Byte getIsStatistics() {
        return isStatistics;
    }

    public void setIsStatistics(Byte isStatistics) {
        this.isStatistics = isStatistics;
    }

    public Byte getIsPartner() {
        return isPartner;
    }

    public void setIsPartner(Byte isPartner) {
        this.isPartner = isPartner;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public String getRegisterPlace() {
        return registerPlace;
    }

    public void setRegisterPlace(String registerPlace) {
        this.registerPlace = registerPlace;
    }

    public BigDecimal getRegisterCapital() {
        return registerCapital;
    }

    public void setRegisterCapital(BigDecimal registerCapital) {
        this.registerCapital = registerCapital;
    }

    public String getOrgRemark() {
        return orgRemark;
    }

    public void setOrgRemark(String orgRemark) {
        this.orgRemark = orgRemark == null ? null : orgRemark.trim();
    }

    public Integer getOrderby() {
        return orderby;
    }

    public void setOrderby(Integer orderby) {
        this.orderby = orderby;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Byte getIsValid() {
        return isValid;
    }

    public void setIsValid(Byte isValid) {
        this.isValid = isValid;
    }

    public Date getInvalidDate() {
        return invalidDate;
    }

    public void setInvalidDate(Date invalidDate) {
        this.invalidDate = invalidDate;
    }

    public String getOrgSimpleName() {
        return orgSimpleName;
    }

    public void setOrgSimpleName(String orgSimpleName) {
        this.orgSimpleName = orgSimpleName;
    }

    public Byte getOrgAttr() {
        return orgAttr;
    }

    public void setOrgAttr(Byte orgAttr) {
        this.orgAttr = orgAttr;
    }

    public Date getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(Date periodStart) {
        this.periodStart = periodStart;
    }

    public Date getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Date periodEnd) {
        this.periodEnd = periodEnd;
    }

    public Long getLegalPerson() {
        return legalPerson;
    }

    public void setLegalPerson(Long legalPerson) {
        this.legalPerson = legalPerson;
    }

    public Byte getIsBranchOffice() {
        return isBranchOffice;
    }

    public void setIsBranchOffice(Byte isBranchOffice) {
        this.isBranchOffice = isBranchOffice;
    }

    public String getOrgAttrName() {
        return orgAttrName;
    }

    public void setOrgAttrName(String orgAttrName) {
        this.orgAttrName = orgAttrName;
    }

    public String getLegalPersonName() {
        return legalPersonName;
    }

    public void setLegalPersonName(String legalPersonName) {
        this.legalPersonName = legalPersonName;
    }

    public Byte getIsPromise() {
        return isPromise;
    }

    public void setIsPromise(Byte isPromise) {
        this.isPromise = isPromise;
    }

    public String getSysOrgIndustryClassifyNames() {
        return sysOrgIndustryClassifyNames;
    }

    public void setSysOrgIndustryClassifyNames(String sysOrgIndustryClassifyNames) {
        this.sysOrgIndustryClassifyNames = sysOrgIndustryClassifyNames;
    }

    public List<SysOrg> getChildren() {
        return children;
    }

    public void setChildren(List<SysOrg> children) {
        this.children = children;
    }

    public List<BasShareholder> getBasShareholderList() {
        return basShareholderList;
    }

    public void setBasShareholderList(List<BasShareholder> basShareholderList) {
        this.basShareholderList = basShareholderList;
    }

    public List<SysOrgShareholder> getSysOrgShareholderList() {
        return sysOrgShareholderList;
    }

    public void setSysOrgShareholderList(List<SysOrgShareholder> sysOrgShareholderList) {
        this.sysOrgShareholderList = sysOrgShareholderList;
    }

    public Long[] getSysOrgIndustryClassifys() {
        return sysOrgIndustryClassifys;
    }

    public void setSysOrgIndustryClassifys(Long[] sysOrgIndustryClassifys) {
        this.sysOrgIndustryClassifys = sysOrgIndustryClassifys;
    }

    @Override
    public String toString() {
        return "SysOrg{" +
                "id=" + id +
                ", pid=" + pid +
                ", orgCode='" + orgCode + '\'' +
                ", orgSimpleName='" + orgSimpleName + '\'' +
                ", orgName='" + orgName + '\'' +
                ", pName='" + pName + '\'' +
                ", orgRemark='" + orgRemark + '\'' +
                ", isStatistics=" + isStatistics +
                ", isPartner=" + isPartner +
                ", registerTime=" + registerTime +
                ", registerPlace='" + registerPlace + '\'' +
                ", registerCapital=" + registerCapital +
                ", orderby=" + orderby +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", isValid=" + isValid +
                ", invalidDate=" + invalidDate +
                ", orgAttr=" + orgAttr +
                ", periodStart=" + periodStart +
                ", periodEnd=" + periodEnd +
                ", legalPerson=" + legalPerson +
                ", industry=" + industry +
                ", isBranchOffice=" + isBranchOffice +
                ", orgAttrName='" + orgAttrName + '\'' +
                ", legalPersonName='" + legalPersonName + '\'' +
                ", isPromise=" + isPromise +
                ", sysOrgIndustryClassifyNames='" + sysOrgIndustryClassifyNames + '\'' +
                ", children=" + children +
                ", basShareholderList=" + basShareholderList +
                ", sysOrgShareholderList=" + sysOrgShareholderList +
                ", sysOrgIndustryClassifys=" + Arrays.toString(sysOrgIndustryClassifys) +
                '}';
    }
}