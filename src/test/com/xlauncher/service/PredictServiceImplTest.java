package com.xlauncher.service.impl;

import com.xlauncher.entity.Predict;
import com.xlauncher.service.PredictService;
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
 * @Date :2018/10/18 0018
 * @Desc :
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml","classpath:spring-*.xml"})
@Transactional
public class PredictServiceImplTest {
    @Autowired
    private PredictService predictService;
    private static Predict predict;
    @Before
    public void init() {
        predict = new Predict();
        predict.setCollectTime(DatetimeUtil.getDate(System.currentTimeMillis()));
        predict.setChannel(1001);
        predict.setSid("1001");
    }
    @Test
    public void listPredict() throws Exception {
        System.out.println("listPredict:" + predictService.listPredict("undefined","undefined","undefined","",1));
    }

    @Test
    public void countPredict() throws Exception {
        System.out.println("countPredict:" + predictService.countPredict("undefined","undefined","undefined",""));
    }

    @Test
    public void insertPredict() throws Exception {
        Map<String, Object> map = new HashMap<>(1);
        System.out.println("insertPredict:" + predictService.insertPredict(map));
    }

    @Test
    public void updatePredict() throws Exception {
        Predict upPre = new Predict();
        upPre.setId(1);
        upPre.setIdentify((float)25.15);
        upPre.setCheck((float)28.55);
        upPre.setStatus(0);
        System.out.println("updatePredict:" + predictService.updatePredict(upPre));
    }

    @Test
    public void countErrPredict() throws Exception {
        System.out.println("countErrPredict:" + predictService.countErrPredict());
    }

    @Test
    public void countCheckPredict() throws Exception {
        System.out.println("countCheckPredict:" + predictService.countCheckPredict());
    }

}