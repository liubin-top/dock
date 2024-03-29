package com.sinosoft.model;

import java.math.BigDecimal;

public class BasShaShareholder {
    private Long id;

    private Long basShareholder;

    private Long shareholder;

    private BigDecimal shareRatio;

    private String shareholderName;//股东名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBasShareholder() {
        return basShareholder;
    }

    public void setBasShareholder(Long basShareholder) {
        this.basShareholder = basShareholder;
    }

    public Long getShareholder() {
        return shareholder;
    }

    public void setShareholder(Long shareholder) {
        this.shareholder = shareholder;
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
}