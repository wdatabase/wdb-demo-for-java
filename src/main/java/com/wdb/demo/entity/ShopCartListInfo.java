package com.wdb.demo.entity;

public class ShopCartListInfo {
    private String proid;
    private String title;
    private double price;
    private long inventory;
    private String imgid;
    private long num;

    public void setProid(String proid){
        this.proid = proid;
    }

    public String getProid(){
        return proid;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public double getPrice(){
        return price;
    }

    public void setNum(long num){
        this.num = num;
    }

    public long getNum(){
        return num;
    }

    public void setImgid(String imgid){
        this.imgid = imgid;
    }

    public String getImgid(){
        return imgid;
    }

    public void setInventory(long inventory){
        this.inventory = inventory;
    }

    public long getInventory(){
        return inventory;
    }
}
