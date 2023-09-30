package com.wdb.demo.entity;

import java.util.ArrayList;

public class ShopProInfo {
    private String uuid;
    private String title;
    private double price;
    private double weight;
    private long inventory;
    private ArrayList<String> tps;
    private String imgid;
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

    public void setPrice(double price){
        this.price = price;
    }

    public double getPrice(){
        return price;
    }

    public void setWeight(double weight){
        this.weight = weight;
    }

    public double getWeight(){
        return weight;
    }

    public void setInventory(long inventory){
        this.inventory = inventory;
    }

    public long getInventory(){
        return inventory;
    }

    public void setTps(ArrayList<String> tps){
        this.tps = tps;
    }

    public ArrayList<String> getTps(){
        return tps;
    }

    public void setImgid(String imgid){
        this.imgid = imgid;
    }

    public String getImgid(){
        return imgid;
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
