package com.wdb.demo.entity;

public class SearchListReq {
    private String o;
    private String title;
    private String score;
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

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setScore(String score){
        this.score = score;
    }

    public String getScore(){
        return score;
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
