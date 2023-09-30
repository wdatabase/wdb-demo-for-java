package com.wdb.demo.entity;

public class ShopBalanceReq {
    private String o;
    private double balance;

    public void setO(String o){
        this.o = o;
    }

    public String getO(){
        return o;
    }

    public void setBalance(double balance){
        this.balance = balance;
    }

    public double getBalance(){
        return balance;
    }
}
