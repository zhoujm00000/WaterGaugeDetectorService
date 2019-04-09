package com.xlauncher.dao;

import com.xlauncher.entity.Predict;
import com.xlauncher.util.DatetimeUtil;
import com.xlauncher.util.ImageUtil;
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
 * @Date :2018/10/19 0019
 * @Desc :
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml","classpath:spring-*.xml"})

public class PredictDaoTest {
    @Autowired
    PredictDao predictDao;
    private static Predict predict;
    @Before
    public void init() {
        predict = new Predict();
        predict.setCollectTime(DatetimeUtil.getDate(System.currentTimeMillis()));
        predict.setChannel(1001);
        predict.setSid("1001");
    }
    @Test
    public void getPredict() throws Exception {
        System.out.println("getPredict:" + predictDao.getPredict(1001));
    }

    @Test
    public void listPredict() throws Exception {
        System.out.println("listPredict:" + predictDao.listPredict("undefined","undefined","undefined","",1));
    }

    @Test
    public void countPredict() throws Exception {
        System.out.println("countPredict:" + predictDao.countPredict("undefined","undefined","undefined",""));
    }

    @Test
    public void insertPredict() throws Exception {
        System.out.println("insertPredict:" + predictDao.insertPredict(predict));
    }

    @Test
    public void updatePredict() throws Exception {
        Predict upPre = new Predict();
        upPre.setId(5003);
        upPre.setStatus(1);
        upPre.setCheck((float) 1);
        System.out.println("updatePredict:" + predictDao.updatePredict(upPre));
    }

    @Test
    public void countPredictByStatus() throws Exception {
        System.out.println("countPredictByStatus:" + predictDao.countPredictByStatus(0));
    }

    @Test
    public void countPredictByStatusNum() throws Exception {
        System.out.println("countPredictByStatusNum:" + predictDao.countPredictByStatusNum(0,2));
    }

    @Test
    public void countPredictByCheck() throws Exception {
        System.out.println("countPredictByCheck:" + predictDao.countPredictByCheck());
    }

    @Test
    public void predictExistence() throws Exception {
        System.out.println("predictExistence:" + predictDao.predictExistence("2017-08-23 12:51:00","4726510357"));
    }

    @Test
    public void getHalfYearPredict() throws Exception {
        System.out.println("getHalfYearPredict:" + predictDao.getHalfYearPredict());
    }

    @Test
    public void getHalfAllYearPredict() throws Exception {
        System.out.println("getHalfAllYearPredict:" + predictDao.getHalfAllYearPredict());
    }
}