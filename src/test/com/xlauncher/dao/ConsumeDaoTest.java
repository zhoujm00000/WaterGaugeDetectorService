package com.xlauncher.dao;

import com.xlauncher.entity.Consume;
import com.xlauncher.service.ConsumeService;
import com.xlauncher.util.DatetimeUtil;
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
 * @Date :2018/10/17 0017
 * @Desc :
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml","classpath:spring-*.xml"})
@Transactional
public class ConsumeDaoTest {
    @Autowired
    private ConsumeDao consumeDao;
    @Autowired
    ConsumeService consumeService;
    private Consume consume;

    @Before
    public void init() {
        consume = new Consume();
        consume.setStartTime(DatetimeUtil.getDate(System.currentTimeMillis()));
        consume.setConsumeTime("600");
    }

    @Test
    public void insertConsume() throws Exception {
        System.out.println("insertConsume：" + consumeDao.insertConsume(consume));
    }

    @Test
    public void avgConsumeTimeByDay() throws Exception {
        System.out.println("listConsume: " + consumeDao.avgConsumeTimeByDay(0));
    }

}