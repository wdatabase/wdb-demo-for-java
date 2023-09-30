package com.wdb.demo.entity;

public class TextReq {
    private String o;
    private String uuid;
    private String title;
    private String content;

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

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setContent(String content){
        this.content = content;
    }

    public String getContent(){
        return content;
    }
}
