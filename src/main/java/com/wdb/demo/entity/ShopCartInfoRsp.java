package com.wdb.demo.entity;

public class ShopCartInfoRsp {
    private long code;
    private String msg;
    private ShopCartInfo info;

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

    public void setInfo(ShopCartInfo info){
        this.info = info;
    }

    public ShopCartInfo getInfo(){
        return info;
    }

}
