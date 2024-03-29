package com.sinosoft.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class BasShareholder {
    private Long id;

    private Byte shareholderType;

    private String shareholderName;

    private String idNumber;

    private Byte isPromise;

    private Byte isUnusual;

    private Byte isIllegal;

    private Byte isInternal;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8:00")
    private Date registerTime;

    private String registerPlace;

    private BigDecimal registerCapital;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8:00")
    private Date periodStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8:00")
    private Date periodEnd;

    private String legalPerson;

    private Integer orderby;

    private Date createDate;

    private Date updateDate;

    private Byte isValid;

    private Date invalidDate;

    private String basShareholderIndustryClassifyNames;//主营业务-行业分类名称

    private Long[] basShareholderIndustryClassifys;//主营业务-行业分类

    private List<BasShaShareholder> basShaShareholderList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Byte getShareholderType() {
        return shareholderType;
    }

    public void setShareholderType(Byte shareholderType) {
        this.shareholderType = shareholderType;
    }

    public String getShareholderName() {
        return shareholderName;
    }

    public void setShareholderName(String shareholderName) {
        this.shareholderName = shareholderName == null ? null : shareholderName.trim();
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber == null ? null : idNumber.trim();
    }

    public Byte getIsPromise() {
        return isPromise;
    }

    public void setIsPromise(Byte isPromise) {
        this.isPromise = isPromise;
    }

    public Byte getIsUnusual() {
        return isUnusual;
    }

    public void setIsUnusual(Byte isUnusual) {
        this.isUnusual = isUnusual;
    }

    public Byte getIsIllegal() {
        return isIllegal;
    }

    public void setIsIllegal(Byte isIllegal) {
        this.isIllegal = isIllegal;
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

    public Byte getIsInternal() {
        return isInternal;
    }

    public void setIsInternal(Byte isInternal) {
        this.isInternal = isInternal;
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

    public String getLegalPerson() {
        return legalPerson;
    }

    public void setLegalPerson(String legalPerson) {
        this.legalPerson = legalPerson;
    }

    public String getBasShareholderIndustryClassifyNames() {
        return basShareholderIndustryClassifyNames;
    }

    public void setBasShareholderIndustryClassifyNames(String basShareholderIndustryClassifyNames) {
        this.basShareholderIndustryClassifyNames = basShareholderIndustryClassifyNames;
    }

    public Long[] getBasShareholderIndustryClassifys() {
        return basShareholderIndustryClassifys;
    }

    public void setBasShareholderIndustryClassifys(Long[] basShareholderIndustryClassifys) {
        this.basShareholderIndustryClassifys = basShareholderIndustryClassifys;
    }

    public List<BasShaShareholder> getBasShaShareholderList() {
        return basShaShareholderList;
    }

    public void setBasShaShareholderList(List<BasShaShareholder> basShaShareholderList) {
        this.basShaShareholderList = basShaShareholderList;
    }
}