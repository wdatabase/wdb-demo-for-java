package com.wdb.demo.entity;

public class VideoInfo {
    private String uuid;
    private String name;
    private String fileName;
    private String contentType;
    private long size;
    private String fileUuid;
    private long createTime;
    private long updateTime;

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
 
    public void setCreateTime(long createTime){
        this.createTime = createTime;
    }

    public long getCreateTime(){
        return createTime;
    }

    public void setUpdateTime(long updateTime){
        this.updateTime = updateTime;
    }

    public long getUpdateTime(){
        return updateTime;
    }
}
