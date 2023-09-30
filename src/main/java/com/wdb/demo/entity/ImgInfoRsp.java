package com.wdb.demo.entity;

public class ImgInfoRsp {
    private long code;
    private String msg;
    private ImgInfo info;

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

    public void setInfo(ImgInfo info){
        this.info = info;
    }

    public ImgInfo getInfo(){
        return info;
    }

}
