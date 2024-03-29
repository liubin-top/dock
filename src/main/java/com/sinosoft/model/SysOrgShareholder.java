package com.sinosoft.model;

import java.math.BigDecimal;

public class SysOrgShareholder {
    private Long id;

    private Long sysOrg;

    private Long basShareholder;

    private BigDecimal shareRatio;

    private String shareholderName;//股东/法人名称

    private String isPromise;//是否被列入失信名单

    private String isUnusual;//是否存在经营异常

    private String isIllegal;//是否被列入严重违法失信企业名单

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

    public Long getBasShareholder() {
        return basShareholder;
    }

    public void setBasShareholder(Long basShareholder) {
        this.basShareholder = basShareholder;
    }

    public BigDecimal getShareRatio() {
        return shareRatio;
    }

    public void setShareRatio(BigDecimal shareRatio) {
        this.shareRatio = shareRatio;
    }

    public String getShareholderName() {
        return shareholderName;
    }

    public void setShareholderName(String shareholderName) {
        this.shareholderName = shareholderName;
    }

    public String getIsPromise() {
        return isPromise;
    }

    public void setIsPromise(String isPromise) {
        this.isPromise = isPromise;
    }

    public String getIsUnusual() {
        return isUnusual;
    }

    public void setIsUnusual(String isUnusual) {
        this.isUnusual = isUnusual;
    }

    public String getIsIllegal() {
        return isIllegal;
    }

    public void setIsIllegal(String isIllegal) {
        this.isIllegal = isIllegal;
    }
}