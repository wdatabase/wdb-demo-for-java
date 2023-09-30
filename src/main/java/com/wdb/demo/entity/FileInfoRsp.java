package com.wdb.demo.entity;

public class FileInfoRsp {
    private long code;
    private String msg;
    private FileInfo info;

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

    public void setInfo(FileInfo info){
        this.info = info;
    }

    public FileInfo getInfo(){
        return info;
    }

}
