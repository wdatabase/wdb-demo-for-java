package com.wdb.demo.entity;

public class ShopListReq {
    private String o;
    private String indexkey;
    private String titlekey;
    private String begin;
    private String end;
    private long offset;
    private long limit;
    private String order;

    public void setO(String o){
        this.o = o;
    }

    public String getO(){
        return o;
    }

    public void setIndexkey(String indexkey){
        this.indexkey = indexkey;
    }

    public String getIndexkey(){
        return indexkey;
    }

    public void setTitlekey(String titlekey){
        this.titlekey = titlekey;
    }

    public String getTitlekey(){
        return titlekey;
    }

    public void setBegin(String begin){
        this.begin = begin;
    }

    public String getBegin(){
        return begin;
    }

    public void setEnd(String end){
        this.end = end;
    }

    public String getEnd(){
        return end;
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
