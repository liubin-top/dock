package com.sinosoft.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.pl.NIP;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BasIdxData {
    private Long id;

    @NotNull(message = "组织不能为控")
    private Long sysOrg;

    private Long project;

    private Byte dataSource;

    private Long basIndustry;
    @JsonFormat(pattern="yyyy-MM",timezone="GMT+8")
    private Date dataDate;

    private Long basIndex;

    private Byte dataType;

    private Long property;

    private BigDecimal dataValue;

    private List<Map> dataValueArr;

    private String projectName;//项目名称

    private String orgName;//机构名称

    private String industryName;//产业名称

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

    public Long getProject() {
        return project;
    }

    public void setProject(Long project) {
        this.project = project;
    }

    public Byte getDataSource() {
        return dataSource;
    }

    public void setDataSource(Byte dataSource) {
        this.dataSource = dataSource;
    }

    public Long getBasIndustry() {
        return basIndustry;
    }

    public void setBasIndustry(Long basIndustry) {
        this.basIndustry = basIndustry;
    }

    public Date getDataDate() {
        return dataDate;
    }

    public void setDataDate(Date dataDate) {
        this.dataDate = dataDate;
    }

    public Long getBasIndex() {
        return basIndex;
    }

    public void setBasIndex(Long basIndex) {
        this.basIndex = basIndex;
    }

    public Byte getDataType() {
        return dataType;
    }

    public void setDataType(Byte dataType) {
        this.dataType = dataType;
    }

    public Long getProperty() {
        return property;
    }

    public void setProperty(Long property) {
        this.property = property;
    }

    public BigDecimal getDataValue() {
        return dataValue;
    }

    public void setDataValue(BigDecimal dataValue) {
        this.dataValue = dataValue;
    }

    public List<Map> getDataValueArr() {
        return dataValueArr;
    }

    public void setDataValueArr(List<Map> dataValueArr) {
        this.dataValueArr = dataValueArr;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    @Override
    public String toString() {
        return "BasIdxData{" +
                "id=" + id +
                ", sysOrg=" + sysOrg +
                ", project=" + project +
                ", dataSource=" + dataSource +
                ", basIndustry=" + basIndustry +
                ", dataDate=" + dataDate +
                ", basIndex=" + basIndex +
                ", dataType=" + dataType +
                ", property=" + property +
                ", dataValue=" + dataValue +
                ", dataValueArr=" + dataValueArr +
                ", projectName='" + projectName + '\'' +
                ", orgName='" + orgName + '\'' +
                ", industryName='" + industryName + '\'' +
                '}';
    }
}