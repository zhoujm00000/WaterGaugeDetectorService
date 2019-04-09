package com.xlauncher.dao;

import com.xlauncher.entity.Configuration;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/17 0017
 * @Desc :服务配置DAO层
 **/
@Service
public interface ConfiDao {

    /**
     * 存储配置信息
     * @param configuration 实例对象
     * @return int
     */
    int insertConfi(Configuration configuration);

    /**
     * 更新修改配置信息
     * @param configuration 实例对象
     * @return int
     */
    int updateConfi(Configuration configuration);

    /**
     * 获取所有配置信息
     * @return List
     */
    List<Configuration> listConfi();

    /**
     * 获取对应项配置信息
     * @param options 配置选项
     * @return
     */
    Configuration getConfi(String options);
}
