package com.wdb.demo.entity;

import java.util.ArrayList;

public class ShopOrderReq {
    private String o;
    private double total;
    private ArrayList<String> ids;
    private ArrayList<Long> nums;
    private ArrayList<Double> prices;

    public void setO(String o){
        this.o = o;
    }

    public String getO(){
        return o;
    }

    public void setTotal(double total){
        this.total = total;
    }

    public double getTotal(){
        return total;
    }

    public void setIds(ArrayList<String> ids){
        this.ids = ids;
    }

    public ArrayList<String> getIds(){
        return ids;
    }

    public void setNums(ArrayList<Long> nums){
        this.nums = nums;
    }

    public ArrayList<Long> getNums(){
        return nums;
    }

    public void setPrices(ArrayList<Double> prices){
        this.prices = prices;
    }

    public ArrayList<Double> getPrices(){
        return prices;
    }

}
