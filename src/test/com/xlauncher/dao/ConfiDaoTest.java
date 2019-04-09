package com.xlauncher.dao;

import com.xlauncher.entity.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @Auther :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/19 0019
 * @Desc :
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml","classpath:spring-*.xml"})
@Transactional
public class ConfiDaoTest {
    @Autowired
    ConfiDao confiDao;
    private static Configuration configuration;
    @Before
    public void init() {
        configuration = new Configuration();
        configuration.setOptions("test");
        Map<String, Object> map = new HashMap<>(1);
        map.put("data","data");
        configuration.setParams(map);
    }
    @Test
    public void insertConfi() throws Exception {
        System.out.println("insertConfi:" + confiDao.insertConfi(configuration));
    }

    @Test
    public void updateConfi() throws Exception {
        Configuration upConfi = new Configuration();
        upConfi.setId(1);
        upConfi.setOptions("MySQL");
        Map<String, Object> map = new HashMap<>(1);
        map.put("port","3306");
        upConfi.setParams(map);
        System.out.println("updateConfi:" + confiDao.updateConfi(upConfi));
    }

    @Test
    public void listConfi() throws Exception {
        System.out.println("listConfi:" + confiDao.listConfi());
    }

    @Test
    public void getConfi() throws Exception {
        System.out.println("getConfi:" + confiDao.getConfi("undefined"));
    }

}