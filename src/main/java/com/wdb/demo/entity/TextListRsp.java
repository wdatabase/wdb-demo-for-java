package com.wdb.demo.entity;

import java.util.ArrayList;

public class TextListRsp {
    private long code;
    private String msg;
    private long total;
    private ArrayList<TextListInfo> list;

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

    public void setList(ArrayList<TextListInfo> list){
        this.list = list;
    }

    public ArrayList<TextListInfo> getList(){
        return list;
    }
}
