package com.wdb.demo.entity;

public class UserRsp {
    private long code;
    private String uid;
    private long time;
    private String sign;
    private String msg;

    public void setCode(long code){
        this.code = code;
    }

    public long getCode(){
        return code;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public String getUid(){
        return uid;
    }

    public void setTime(long time){
        this.time = time;
    }

    public long getTime(){
        return time;
    }

    public void setSign(String sign){
        this.sign = sign;
    }

    public String getSign(){
        return sign;
    }

    public void setMsg(String msg){
        this.msg = msg;
    }

    public String getMsg(){
        return msg;
    }
}
