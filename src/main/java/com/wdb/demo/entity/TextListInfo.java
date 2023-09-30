package com.wdb.demo.entity;

public class TextListInfo {
    private String uuid;
    private String title;
    private long time;

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

    public void setTime(long time){
        this.time = time;
    }

    public long getTime(){
        return time;
    }
}
