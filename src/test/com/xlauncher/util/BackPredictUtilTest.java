package com.xlauncher.util;

import com.xlauncher.dao.PredictDao;
import com.xlauncher.entity.Predict;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @Auther :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/30 0030
 * @Desc :
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml","classpath:spring-*.xml"})
@Transactional
public class BackPredictUtilTest {
    @Autowired
    BackPredictUtil backPredictUtil;
    @Autowired
    JDBCUtil jdbcUtil;
    @Autowired
    PredictDao predictDao;

    @Test
    public void back() throws Exception {
        List<Predict> list = new ArrayList<>(1);
        for (int i=50;i<=80;i++) {
            System.out.println("iiiiiii:" + i);
            Predict predict = predictDao.getPredict(i);
            System.out.println("___predict." + predict);
            list.add(predict);
        }
        System.out.println("____size." + list.size());
        backPredictUtil.batchBack("");
    }

}