package com.wdb.demo.entity;

public class ShopInfo {
    private String uuid;
    private String uid;
    private double balance;
    private double point;
    private long createTime;
    private long updateTime;

    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    public String getUuid(){
        return uuid;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public String getUid(){
        return uid;
    }

    public void setPoint(double point){
        this.point = point;
    }

    public double getPoint(){
        return point;
    }

    public void setBalance(double balance){
        this.balance = balance;
    }

    public double getBalance(){
        return balance;
    }

    public void setCreateTime(long createTime){
        this.createTime = createTime;
    }

    public long getCreateTime(){
        return createTime;
    }

    public void setUpdateTime(long updateTime){
        this.updateTime = updateTime;
    }

    public long getUpdateTime(){
        return updateTime;
    }
}
