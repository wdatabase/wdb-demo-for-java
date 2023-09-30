package com.wdb.demo.entity;

import java.util.ArrayList;

public class ShopCartListRsp {
    private long code;
    private String msg;
    private double total;
    private ArrayList<ShopCartListInfo> listinfo;

    public void setTotal(double total){
        this.total = total;
    }

    public double getTotal(){
        return total;
    }

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

    public void setListinfo(ArrayList<ShopCartListInfo> listinfo){
        this.listinfo = listinfo;
    }

    public ArrayList<ShopCartListInfo> getListinfo(){
        return listinfo;
    }

}
