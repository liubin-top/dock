package com.sinosoft.vo;

import java.util.List;

public class EchartTreeDataVO {
    private String id;
    private String pid;
    private String name;//节点名字
    private String value;
    private String line;//线的名字
    private boolean expand;
    private List<EchartTreeDataVO> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<EchartTreeDataVO> getChildren() {
        return children;
    }

    public void setChildren(List<EchartTreeDataVO> children) {
        this.children = children;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }
}
