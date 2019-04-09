package com.xlauncher.service.impl;

import com.xlauncher.dao.ConfiDao;
import com.xlauncher.entity.Configuration;
import com.xlauncher.service.ConfiService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/17 0017
 * @Desc :服务配置实现类
 **/
@Service
public class ConfiServiceImpl implements ConfiService {

    private final ConfiDao confiDao;
    private static Logger logger = Logger.getLogger(ConfiServiceImpl.class);

    @Autowired
    public ConfiServiceImpl(ConfiDao confiDao) {
        this.confiDao = confiDao;
    }

    /**
     * 存储配置信息
     * @param configuration 实例对象
     * @return int
     */
    @Override
    public int insertConfi(Configuration configuration) {
        logger.info("存储配置信息insertConfi:" + configuration);
        return confiDao.insertConfi(configuration);
    }

    /**
     * 更新修改配置信息
     * @param list 实例对象
     * @return int
     */
    @Override
    public int updateConfi(List<Configuration> list) {
        logger.info("更新配置信息updateConfi:" + list);
        final int[] code = {0};
        list.forEach(configuration -> {
            code[0] = confiDao.updateConfi(configuration);
            logger.info("configuration:" + configuration + ",code:" + code[0]);
        });
        logger.info("result:" + code[0]);
        return code[0];
    }

    /**
     * 获取所有配置信息
     * @return List
     */
    @Override
    public List<Configuration> listConfi() {
        logger.info("获取所有配置信息List<Configuration>");
        return confiDao.listConfi();
    }
}
