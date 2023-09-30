package com.wdb.demo.entity;

import java.util.ArrayList;

public class ShopCartInfo {
    private String uuid;
    private String uid;
    private ArrayList<String> ids;
    private ArrayList<Long> nums;
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
