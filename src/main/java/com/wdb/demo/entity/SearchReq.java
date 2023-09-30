package com.wdb.demo.entity;

public class SearchReq {
    private String o;
    private String uuid;
    private String title;
    private String content;
    private float score;

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

    public void setScore(float score){
        this.score = score;
    }

    public float getScore(){
        return score;
    }
}
