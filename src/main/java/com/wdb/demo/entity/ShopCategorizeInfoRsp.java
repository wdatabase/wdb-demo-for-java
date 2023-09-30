package com.wdb.demo.entity;

public class ShopCategorizeInfoRsp {
    private long code;
    private String msg;
    private ShopCategorizeInfo info;

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

    public void setInfo(ShopCategorizeInfo info){
        this.info = info;
    }

    public ShopCategorizeInfo getInfo(){
        return info;
    }

}
