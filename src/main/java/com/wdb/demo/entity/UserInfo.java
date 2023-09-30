package com.wdb.demo.entity;

public class UserInfo {
    private String uuid;
    private String user;
    private String pwd;
    private String mail;
    private long createTime;
    private long updateTime;

    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    public String getUuid(){
        return uuid;
    }

    public void setUser(String user){
        this.user = user;
    }

    public String getUser(){
        return user;
    }

    public void setPwd(String pwd){
        this.pwd = pwd;
    }

    public String getPwd(){
        return pwd;
    }

    public void setMail(String mail){
        this.mail = mail;
    }

    public String getMail(){
        return mail;
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
