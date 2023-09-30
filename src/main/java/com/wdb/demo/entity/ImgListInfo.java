package com.wdb.demo.entity;

public class ImgListInfo {
    private String uuid;
    private String name;
    private String fileName;
    private String contentType;
    private long size;
    private String fileUuid;
    private long time;

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

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    public String getFileName(){
        return fileName;
    }

    public void setContentType(String contentType){
        this.contentType = contentType;
    }

    public String getContentType(){
        return contentType;
    }

    public void setSize(long size){
        this.size = size;
    }

    public long getSize(){
        return size;
    }

    public void setFileUuid(String fileUuid){
        this.fileUuid = fileUuid;
    }

    public String getFileUuid(){
        return fileUuid;
    }

    public void setTime(long time){
        this.time = time;
    }

    public long getTime(){
        return time;
    }
}
