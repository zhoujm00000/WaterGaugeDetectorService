package com.xlauncher.service;

import com.xlauncher.entity.Configuration;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/17 0017
 * @Desc :
 **/
@Service
public interface ConfiService {

    /**
     * 存储配置信息
     * @param configuration 实例对象
     * @return int
     */
    int insertConfi(Configuration configuration);

    /**
     * 更新修改配置信息
     * @param list 实例对象
     * @return int
     */
    int updateConfi(List<Configuration> list);

    /**
     * 获取所有配置信息
     * @return List
     */
    List<Configuration> listConfi();
}
