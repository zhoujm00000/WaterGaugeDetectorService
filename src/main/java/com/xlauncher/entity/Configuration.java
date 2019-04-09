package com.xlauncher.entity;

import java.util.Map;

/**
 * @author baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/17 0017
 * @Desc :服务配置实体类
 **/
public class Configuration {
    /** 主键id*/
    private Integer id;
    /** 配置选项*/
    private String options;
    /** 配置参数*/
    private Map<String, Object> params;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "id=" + id +
                ", options='" + options + '\'' +
                ", params=" + params +
                '}';
    }
}
