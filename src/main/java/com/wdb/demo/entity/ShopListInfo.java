package com.wdb.demo.entity;

public class ShopListInfo {
    private String uuid;
    private String title;
    private long time;
    private float score;

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

    public void setScore(float score){
        this.score = score;
    }

    public float getScore(){
        return score;
    }

    public void setTime(long time){
        this.time = time;
    }

    public long getTime(){
        return time;
    }
}
