package com.xlauncher.util;

import com.xlauncher.dao.PredictDao;
import com.xlauncher.entity.Predict;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Auther :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/18 0018
 * @Desc :
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml","classpath:spring-*.xml"})
@Transactional
public class SaveErrImgUtilTest {
    @Autowired
    SaveErrImgUtil saveErrImgUtil;
    @Autowired
    PredictDao predictDao;
    @Test
    public void saveErrImg() throws Exception {
        Predict predict = predictDao.getPredict(1001011);
        saveErrImgUtil.saveErrImg(predict.getSource(),1001);
    }

    @Test
    public void run() throws Exception {
        saveErrImgUtil.run();
    }
}