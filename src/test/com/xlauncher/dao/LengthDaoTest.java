package com.xlauncher.dao;

import com.xlauncher.entity.Length;
import org.junit.Before;
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
 * @Date :2018/11/1 0001
 * @Desc :
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml","classpath:spring-*.xml"})
@Transactional
public class LengthDaoTest {
    @Autowired
    LengthDao lengthDao;
    private static Length length;
    @Before
    public void init() {
        length = new Length();
        length.setSid("111111111");
        length.setChannel(2);
        length.setHeight(100);
    }
    @Test
    public void insertLength() throws Exception {
        System.out.println("insertLength:" + lengthDao.insertLength(length));
    }

    @Test
    public void updateLength() throws Exception {
        Length up = new Length();
        up.setId(11);
        up.setSid("111111111");
        up.setChannel(2);
        up.setHeight(100);
        System.out.println("updateLength:" + lengthDao.updateLength(up));
    }

    @Test
    public void getLength() throws Exception {
        System.out.println("getLength:" + lengthDao.getLength("undefined",1));
    }

    @Test
    public void getLengthListForExcel() throws Exception {
        System.out.println("getLengthListForExcel:" + lengthDao.getLengthListForExcel("undefined",1));
    }

    @Test
    public void getLengthList() throws Exception {
        System.out.println("getLengthList:" + lengthDao.getLengthList("undefined",1,1));
    }

    @Test
    public void count() throws Exception {
        System.out.println("count:" + lengthDao.count("undefined",1));
    }

}