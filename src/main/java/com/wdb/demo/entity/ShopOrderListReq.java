package com.wdb.demo.entity;

public class ShopOrderListReq {
    private String o;
    private String titlekey;
    private long offset;
    private long limit;
    private String order;

    public void setO(String o){
        this.o = o;
    }

    public String getO(){
        return o;
    }

    public void setTitlekey(String titlekey){
        this.titlekey = titlekey;
    }

    public String getTitlekey(){
        return titlekey;
    }

    public void setOffset(long offset){
        this.offset = offset;
    }

    public long getOffset(){
        return offset;
    }

    public void setLimit(long limit){
        this.limit = limit;
    }

    public long getLimit(){
        return limit;
    }

    public void setOrder(String order){
        this.order = order;
    }

    public String getOrder(){
        return order;
    }
}
