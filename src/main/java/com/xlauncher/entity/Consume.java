package com.xlauncher.entity;

/**
 * @author baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/17 0017
 * @Desc :耗时统计实体类
 **/
public class Consume {
    /** 主键id*/
    private Integer id;
    /** 读取第三方mysql数据开始时间（例如：2018-10-17 15:26:25）*/
    private String startTime;
    /** 读取一轮数据所消耗的时间，单位秒（例如：660）*/
    private String consumeTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(String consumeTime) {
        this.consumeTime = consumeTime;
    }

    @Override
    public String toString() {
        return "Consume{" +
                "id=" + id +
                ", startTime='" + startTime + '\'' +
                ", consumeTime='" + consumeTime + '\'' +
                '}';
    }
}
