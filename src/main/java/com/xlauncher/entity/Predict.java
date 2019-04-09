package com.xlauncher.entity;

import java.util.Arrays;

/**
 * @author baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/17 0017
 * @Desc :预测信息实体类
 **/
public class Predict {
    /** 主键id*/
    private Integer id;
    /** 采集时间*/
    private String collectTime;
    /** 通道编号*/
    private Integer channel;
    /** 站点编号*/
    private String sid;
    /** 预测数据值*/
    private Float identify;
    /** 反馈数据值*/
    private Float check;
    /** 图片资源*/
    private byte[] source;
    /** 图片状态*/
    private Integer status;
    /** 缩略图资源*/
    private byte[] picture;
    /** 预测异常图片描述*/
    private String identifyDescription;
    /** 反馈异常图片描述*/
    private String checkDescription;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
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

    public Float getIdentify() {
        return identify;
    }

    public void setIdentify(Float identify) {
        this.identify = identify;
    }

    public Float getCheck() {
        return check;
    }

    public void setCheck(Float check) {
        this.check = check;
    }

    public byte[] getSource() {
        if (source != null) {
            return source.clone();
        }
        return null;
    }

    public void setSource(byte[] source) {
        if (source != null) {
            this.source = source.clone();
        } else {
            this.source = null;
        }
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public byte[] getPicture() {
        if (picture != null) {
            return picture.clone();
        }
        return null;
    }

    public void setPicture(byte[] picture) {
        if (picture != null) {
            this.picture = picture.clone();
        } else {
            this.picture = null;
        }
    }

    public String getIdentifyDescription() {
        return identifyDescription;
    }

    public void setIdentifyDescription(String identifyDescription) {
        this.identifyDescription = identifyDescription;
    }

    public String getCheckDescription() {
        return checkDescription;
    }

    public void setCheckDescription(String checkDescription) {
        this.checkDescription = checkDescription;
    }

    @Override
    public String toString() {
        if (source !=null && picture != null) {
            return "Predict{" +
                    "id=" + id +
                    ", collectTime='" + collectTime + '\'' +
                    ", channel=" + channel +
                    ", sid='" + sid + '\'' +
                    ", identify=" + identify +
                    ", check=" + check +
                    ", source.length=" + source.length +
                    ", status=" + status +
                    ", picture.length=" + picture.length +
                    ", identifyDescription=" + identifyDescription +
                    ", checkDescription=" + checkDescription +
                    '}';
        } else {
            return "Predict{" +
                    "id=" + id +
                    ", collectTime='" + collectTime + '\'' +
                    ", channel=" + channel +
                    ", sid='" + sid + '\'' +
                    ", identify=" + identify +
                    ", check=" + check +
                    ", source=" + Arrays.toString(source) +
                    ", status=" + status +
                    ", picture=" + Arrays.toString(picture) +
                    ", identifyDescription=" + identifyDescription +
                    ", checkDescription=" + checkDescription +
                    '}';
        }

    }

}
