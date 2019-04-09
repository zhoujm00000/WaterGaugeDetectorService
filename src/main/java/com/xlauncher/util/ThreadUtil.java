package com.xlauncher.util;

import com.xlauncher.dao.ConfiDao;
import com.xlauncher.service.PredictService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/19 0019
 * @Desc :定时任务执行调用第三方数据
 **/
@Component
public class ThreadUtil implements Runnable{
    private int num = 0;
    private ConfiDao confiDao;
    private JDBCUtil jdbcUtil;
    private PredictService predictService;
    private static Logger logger = Logger.getLogger(ThreadUtil.class);

    @Autowired
    public ThreadUtil(ConfiDao confiDao, JDBCUtil jdbcUtil,PredictService predictService) {
        this.confiDao = confiDao;
        this.jdbcUtil = jdbcUtil;
        this.predictService = predictService;
    }
    private static volatile String timeStamp = null;
    private static volatile boolean isFirstGetTime = true;
    private static ScheduledExecutorService service;

    /**
     * 比较timeStamp,判断哪个作为timeStamp的值
     */
    String getTimeStamp() {
        // 先判断静态timeStamp
        if (timeStamp != null) {
            try {
                // 先获取本地数据库最新时间，如果没有则从第三方数据库获取
                String timeStampNew = predictService.getCollectTime1();
                if (timeStampNew != null) {
                    timeStamp = timeStampNew;
                    logger.info("a.___>timeStamp不为空，获取本地数据库最新时间!" + timeStamp);
                } else {
                    logger.info("a.___>timeStamp不为空，获取本地数据库最新时间为空!" + timeStamp);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("err." + e);
            }
        } else {
            // 获取本地数据库最新时间
            timeStamp = predictService.getCollectTime1();
            System.out.println("1." + timeStamp);
            if (timeStamp != null) {
                logger.info("b.___>获取本地数据库最新时间!" + timeStamp);
                return timeStamp;
            } else {
                try {
                    // 获取第三方数据库最新时间
                    timeStamp = jdbcUtil.getTop1CollectTime();
                    System.out.println("2." + timeStamp);
                    // isFirstGetTime为true表示第一次读取第三方数据库，且数据库有数据，
                    // 说明该数据库中所有数据都是老数据，不进行拉取
                    if (timeStamp != null && !isFirstGetTime) {
                        // 获取所有数据，获取最老的时间
                        timeStamp = jdbcUtil.getFirst1CollectTime();
                        System.out.println("3." + timeStamp);
                        logger.info("c.___>获取第三方数据库最新时间!" + timeStamp);
                    }
                    isFirstGetTime = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("getTop1CollectTime_Err." + e);
                }
            }
        }
        return timeStamp;
    }

    /**
     * 定时执行任务
     */
    void timeTask() {
        long period = Long.parseLong((String)confiDao.getConfi("Time").getParams().get("data"));
        Runnable runnable = () -> {
            num++;
            logger.info(" 1___.调用接口读取数据period:" + period + "分钟.  次数num:" + num);
            try {
                jdbcUtil.server(num);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Err." + e);
            }
        };
        service = Executors
                .newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, 0, period, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        logger.info("___.run.compare");
        // 当用户配置服务时，调用此方法，立即关闭正在执行的定时任务
        service.shutdownNow();
        timeTask();
    }

//     /**
//     * 比较timeStamp,判断哪个作为timeStamp的值
//     */
//    private String compare() {
//        // 先判断静态timeStamp
//        if (timeStamp != null) {
//            // 获去本地数据库最新时间
//            try {
//                String timeStampNew = predictService.getCollectTime1();
//                if (timeStampNew != null) {
//                    timeStamp = timeStampNew;
//                }
//                timeTask();
//                logger.info("根据目前这个时间去第三方数据库读数据预测!" + timeStampNew);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                logger.error("err." + e);
//            }
//        } else {
//            // 获取本地数据库最新时间
//            timeStamp = predictService.getCollectTime1();
//            if (timeStamp != null) {
//                timeTask();
//                logger.info("b.___>获取本地数据库最新时间!" + timeStamp);
//            } else {
//                try {
//                    // 获取第三方数据库最新时间
//                    timeStamp = jdbcUtil.getTop1CollectTime();
//                    timeTask();
//                    if (timeStamp != null) {
//                        //timeTask(timeStamp);
//                        logger.info("c.___>获取第三方数据库最新时间!" + timeStamp);
//                    } else {
//                        //timeTask(null);
//                        // 获取第三方数据库最新时间为空，全量获取数据
//                        int result = jdbcUtil.getAllImageInfo();
//                        logger.info("d.___>全量获取第三方数据.result=" + result);
//                        // 定义一个result，如果全量获取到数据result=1，从数据库获取最新时间并执行timeTask；
//                        // 如果没有获取导数据result=0，再次执行compare方法直到有数据为止。
//                        if (result != 0) {
//                            logger.info("e.___>获取本地数据库最新时间并执行timeTask");
//                            timeTask();
//                        } else {
//                            logger.info("e.___>线程等待.方法重置");
//                            //Thread.sleep(60*1000*10);
//                            run();
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    logger.error("getTop1CollectTime_Err." + e);
//                }
//            }
//        }
//        return timeStamp;
//    }
}
