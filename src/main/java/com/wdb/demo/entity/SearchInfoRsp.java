package com.wdb.demo.entity;

public class SearchInfoRsp {
    private long code;
    private String msg;
    private SearchInfo info;

    public void setCode(long code){
        this.code = code;
    }

    public long getCode(){
        return code;
    }

    public void setMsg(String msg){
        this.msg = msg;
    }

    public String getMsg(){
        return msg;
    }

    public void setInfo(SearchInfo info){
        this.info = info;
    }

    public SearchInfo getInfo(){
        return info;
    }

}
