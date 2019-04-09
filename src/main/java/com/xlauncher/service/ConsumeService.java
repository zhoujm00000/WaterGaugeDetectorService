package com.xlauncher.service;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/17 0017
 * @Desc :
 **/
@Service
public interface ConsumeService {
    /**
     * 存储一轮数据读取消耗时间
     * @param objectMap 实例对象
     * @return int
     */
    int insertConsume(Map<String, Object> objectMap);

    /**
     * 每一天平均一轮数据读取消耗时间
     * @return Map
     */
    Map<String, Object> avgConsumeTimeByDay();
}
