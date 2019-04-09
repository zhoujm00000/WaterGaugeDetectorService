package com.xlauncher.service.impl;

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
public class ConsumeServiceImplTest {
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
        Map<String, Object> map = new HashMap<>(1);
        System.out.println("insertConsume：" + consumeService.insertConsume(map));
    }

    @Test
    public void avgConsumeTimeByDay() throws Exception {
        System.out.println("avgConsumeTimeByDay：" + consumeService.avgConsumeTimeByDay());
    }

}