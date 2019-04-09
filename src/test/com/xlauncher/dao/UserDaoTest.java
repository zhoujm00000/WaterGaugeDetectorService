package com.xlauncher.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * @Auther :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/17 0017
 * @Desc :
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml","classpath:spring-*.xml"})
@Transactional
public class UserDaoTest {
    @Autowired
    UserDao userDao;
    @Test
    public void getUserByAccount() throws Exception {
        System.out.println("getUserByAccount:" + userDao.getUserByAccount("admin"));
    }

    @Test
    public void saveToken() throws Exception {
        System.out.println("saveToken:" + userDao.saveToken("token"));
    }

}