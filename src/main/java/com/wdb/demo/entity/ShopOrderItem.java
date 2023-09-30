package com.wdb.demo.entity;

public class ShopOrderItem {
    private String uuid;
    private String title;
    private String imgid;
    private double price;
    private long num;
    private long createTime;
    private long updateTime;

    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    public String getUuid(){
        return uuid;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setImgid(String imgid){
        this.imgid = imgid;
    }

    public String getImgid(){
        return imgid;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public double getPrice(){
        return price;
    }

    public void setNum(long num){
        this.num = num;
    }

    public long getNum(){
        return num;
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
