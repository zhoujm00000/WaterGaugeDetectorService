package com.xlauncher.util;

import com.xlauncher.dao.PredictDao;
import com.xlauncher.entity.Predict;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/29 0029
 * @Desc :将预测结果返回给第三方
 **/
@Component
public class BackPredictUtil {
    @Autowired
    private JDBCUtil jdbcUtil;
    @Autowired
    private PredictDao predictDao;
    private static final Logger logger = Logger.getLogger(BackPredictUtil.class);


    /**
     * 将预测值存入数据库
     *
     * @param predict predict
     */
    public void back(Predict predict) throws Exception {
        logger.info("18.___将预测结果返回给WaterResource数据库!" + predict);
        jdbcUtil.backServer(predict);
    }

    /**
     * 将预测值存入数据库支持批量存储操作
     *
     * @param startTime 查询的开始时间 2018-11-18 10:48:00
     */
    void batchBack(String startTime) {
        List<Predict> lists = predictDao.getPredictByTime(startTime);
        logger.info("17___.batchBack将预测结果批量返回第三方数据库.lists.size." + lists.size());
        lists.forEach(predict -> {
            try {
                jdbcUtil.backServer(predict);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
