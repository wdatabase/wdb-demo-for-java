package com.wdb.demo.entity;

public class TextInfo {
    private String uuid;
    private String title;
    private String content;
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

    public void setContent(String content){
        this.content = content;
    }

    public String getContent(){
        return content;
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
