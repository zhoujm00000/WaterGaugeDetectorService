package com.xlauncher.dao;

import com.xlauncher.entity.Consume;
import org.springframework.stereotype.Service;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/17 0017
 * @Desc :耗时统计DAO层
 **/
@Service
public interface ConsumeDao {

    /**
     * 存储一轮数据读取消耗时间
     * @param consume 实例对象
     * @return int
     */
    int insertConsume(Consume consume);

    /**
     * 清空30天以上的数据
     *
     * @return
     */
    int emptyMonth();

    /**
     *
     * @return
     */
    int countMonth();

    /**
     * 每一天平均一轮数据读取消耗时间
     * @param num 某一天
     * @return String
     */
    String avgConsumeTimeByDay(int num);

    /**
     * 每一天一轮数据读取消耗时间max
     * @param num 某一天
     * @return String
     */
    String maxConsumeTimeByDay(int num);

    /**
     * 每一天一轮数据读取消耗时间min
     * @param num 某一天
     * @return String
     */
    String minConsumeTimeByDay(int num);
}
