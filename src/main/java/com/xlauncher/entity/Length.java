package com.xlauncher.entity;

/**
 * @author baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/25 0025
 * @Desc :水尺总长实体类
 **/
public class Length {
    /** 主键id*/
    private Integer id;
    /** 通道编号*/
    private Integer channel;
    /** 站点编号*/
    private String sid;
    /** 总长度*/
    private Integer height;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Length{" +
                "id=" + id +
                ", channel=" + channel +
                ", sid='" + sid + '\'' +
                ", height=" + height +
                '}';
    }
}
