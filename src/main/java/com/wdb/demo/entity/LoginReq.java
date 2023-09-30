package com.wdb.demo.entity;

public class LoginReq {
    private String user;
    private String pwd;

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
}
