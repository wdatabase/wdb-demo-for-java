package com.wdb.demo.entity;

public class ShopCategorizeReq {
    private String o;
    private String uuid;
    private String name;
    private int sort;

    public void setO(String o){
        this.o = o;
    }

    public String getO(){
        return o;
    }

    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    public String getUuid(){
        return uuid;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setSort(int sort){
        this.sort = sort;
    }

    public int getSort(){
        return sort;
    }
}
