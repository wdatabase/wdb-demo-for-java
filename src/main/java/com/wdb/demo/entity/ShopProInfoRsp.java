package com.wdb.demo.entity;

public class ShopProInfoRsp {
    private long code;
    private String msg;
    private ShopProInfo info;

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

    public void setInfo(ShopProInfo info){
        this.info = info;
    }

    public ShopProInfo getInfo(){
        return info;
    }

}
