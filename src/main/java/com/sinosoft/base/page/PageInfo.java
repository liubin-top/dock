package com.sinosoft.base.page;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

public class PageInfo {
    public static final int DEFAULT_PAGE_SIZE = 10;
    protected int limit = DEFAULT_PAGE_SIZE; // 每页记录数 -默认为DEFAULT_PAGE_SIZE
    protected long total = -1; // 总记录数, 默认为-1, 表示需要查询
    protected int pageNum = 1; // 当前页
    protected String sort; // 排序字段-属性
    protected String order; // 排序字段-排序方式 asc desc
    protected HashMap<String, String> searchMap; // 查询字段
    protected HashMap<String, String> searchMapEncode; // 查询字段 ，其中传过来需要转码+拼接模糊查询的字段必须存在对象searchMapEncode中
    protected List rows; // 当前页记录List形式
    protected String orderby;//需要传到sql的排序字段，由前台的sort+order拼接而成

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getOrderby() {
        if(sort!=null&&sort!=""){
            this.orderby = sort+" "+order;
        }
        return orderby;
    }
    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }
    public int getLimit() {
        return limit;
    }
    public void setLimit(int limit) {
        this.limit = limit;
    }
    public long getTotal() {
        return total;
    }
    public void setTotal(long total) {
        this.total = total;
    }
    public String getSort() {
        return sort;
    }
    public void setSort(String sort) {
        this.sort = sort;
    }
    public String getOrder() {
        return order;
    }
    public void setOrder(String order) {
        this.order = order;
    }
    public HashMap<String, String> getSearchMap() {
        if(searchMap==null){
            searchMap=new HashMap<String, String>();
            if(searchMapEncode!=null){
                String s;
                StringBuffer sb;
                for (String key : searchMapEncode.keySet()) {
                    if(searchMapEncode.get(key)!=null){
                        try {
                            s=(String)searchMapEncode.get(key);
                            if(s!=null){
                                s=java.net.URLDecoder.decode(s,"UTF-8");
                                s=s.replaceAll(" ", "");
                                if(!s.equals("")){
//									sb = new StringBuffer();
//									sb.append("%");
//									for(int i=0;i<s.length();i++){
//										sb.append(s.charAt(i)+"%");
//									}
                                    searchMap.put(key,"%"+s.toString()+"%");
                                }
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            searchMapEncode=null;
        }
        return searchMap;
    }
    public List getRows() {
        return rows;
    }
    public void setRows(List rows) {
        this.rows = rows;
    }
    public HashMap<String, String> getSearchMapEncode() {
        return searchMapEncode;
    }
    public void setSearchMapEncode(HashMap<String, String> searchMapEncode) {
        this.searchMapEncode = searchMapEncode;
    }
    public void setSearchMap(HashMap<String, String> searchMap) {
        this.searchMap = searchMap;
    }

}
