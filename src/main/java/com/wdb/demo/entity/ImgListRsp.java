package com.wdb.demo.entity;

import java.util.ArrayList;

public class ImgListRsp {
    private long code;
    private String msg;
    private long total;
    private ArrayList<ImgListInfo> list;

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

    public void setTotal(long total){
        this.total = total;
    }

    public long getTotal(){
        return total;
    }

    public void setList(ArrayList<ImgListInfo> list){
        this.list = list;
    }

    public ArrayList<ImgListInfo> getList(){
        return list;
    }
}
