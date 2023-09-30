package com.wdb.demo.entity;

import java.util.ArrayList;

public class ShopProListRsp {
    private long code;
    private String msg;
    private long total;
    private ArrayList<ShopProInfo> list;

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

    public void setList(ArrayList<ShopProInfo> list){
        this.list = list;
    }

    public ArrayList<ShopProInfo> getList(){
        return list;
    }
}
