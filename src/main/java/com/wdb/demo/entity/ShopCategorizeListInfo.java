package com.wdb.demo.entity;

public class ShopCategorizeListInfo implements Comparable<ShopCategorizeListInfo> {
    private String uuid;
    private String name;
    private int sort;

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

    @Override
    public int compareTo(ShopCategorizeListInfo scl){
        return scl.sort - this.sort;
    }

}
