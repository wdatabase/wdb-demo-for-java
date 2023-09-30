package com.wdb.demo.entity;

public class TextInfoRsp {
    private long code;
    private String msg;
    private TextInfo info;

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

    public void setInfo(TextInfo info){
        this.info = info;
    }

    public TextInfo getInfo(){
        return info;
    }

}
