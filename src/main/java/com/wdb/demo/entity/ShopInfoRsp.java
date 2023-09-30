package com.wdb.demo.entity;

public class ShopInfoRsp {
    private long code;
    private String msg;
    private ShopInfo info;

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

    public void setInfo(ShopInfo info){
        this.info = info;
    }

    public ShopInfo getInfo(){
        return info;
    }

}
