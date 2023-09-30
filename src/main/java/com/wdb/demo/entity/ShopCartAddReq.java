package com.wdb.demo.entity;

public class ShopCartAddReq {
    private String o;
    private String uuid;
    private Long num;

    public void setO(String o){
        this.o = o;
    }

    public String getO(){
        return o;
    }

    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    public String getUuid(){
        return uuid;
    }

    public void setNum(Long num){
        this.num = num;
    }

    public Long getNum(){
        return num;
    }
    
}
