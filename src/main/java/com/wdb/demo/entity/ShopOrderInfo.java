package com.wdb.demo.entity;

import java.util.ArrayList;

public class ShopOrderInfo {
    private String uuid;
    private String title;
    private String imgid;
    private double total;
    private ArrayList<String> ids;
    private ArrayList<Long> nums;
    private ArrayList<Double> prices;
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

    public void setTotal(double total){
        this.total = total;
    }

    public double getTotal(){
        return total;
    }

    public void setIds(ArrayList<String> ids){
        this.ids = ids;
    }

    public ArrayList<String> getIds(){
        return ids;
    }

    public void setNums(ArrayList<Long> nums){
        this.nums = nums;
    }

    public ArrayList<Long> getNums(){
        return nums;
    }

    public void setPrices(ArrayList<Double> prices){
        this.prices = prices;
    }

    public ArrayList<Double> getPrices(){
        return prices;
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
