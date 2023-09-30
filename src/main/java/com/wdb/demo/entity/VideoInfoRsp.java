package com.wdb.demo.entity;

public class VideoInfoRsp {
    private long code;
    private String msg;
    private VideoInfo info;

    public void setCode(long code){
        this.code = code;
    }

    public long getCode(){
        return code;
    }

    public void setMsg(String msg){
        this.msg = msg;
    }

    public String getMsg(){
        return msg;
    }

    public void setInfo(VideoInfo info){
        this.info = info;
    }

    public VideoInfo getInfo(){
        return info;
    }

}
