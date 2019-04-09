package com.xlauncher.util;

import com.xlauncher.dao.ConfiDao;
import com.xlauncher.dao.PredictDao;
import com.xlauncher.entity.Predict;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.stream.FileImageOutputStream;
import java.io.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/18 0018
 * @Desc :存储半年以上的异常图片、清空资源，同时达到条件进行压缩并删除原文件
 **/
@Component
public class SaveErrImgUtil implements Runnable{
    private final ConfiDao confiDao;
    private final PredictDao predictDao;
    private static final int COUNT = 2000;
    private static Logger logger = Logger.getLogger(SaveErrImgUtil.class);
    private int status;
    /**
     * 通过注解构造函数的形式注入bean, 避免现场注射
     * @param confiDao confiDao
     */
    @Autowired
    public SaveErrImgUtil(ConfiDao confiDao,PredictDao predictDao) {
        this.confiDao = confiDao;
        this.predictDao = predictDao;
    }


    /**
     * 将半年以上的异常图片进行存储
     */
    private void compare() {
        List<Predict> predictLists;
        if (status == 0) {
            logger.info("  1 compare.status is " + status);
            predictLists = predictDao.getHalfAllYearPredict();
        } else {
            logger.info("  2 compare.status is " + status);
            predictLists = predictDao.getHalfYearPredict();
        }

        logger.info("取出半年以上的异常图片进行存储predictLists:" + predictLists.size());
        if (predictLists.size() == 0) {
            logger.info("predictLists.size()==0 return!");
        } else {
            predictLists.forEach(predict -> {
                logger.info("predict:" + predict.toString());
                byte[] data = predict.getSource();
                try {
                    saveErrImg(data, predict.getId());
                    status = predictDao.emptySource(predict.getId());
                    logger.info("清空当前数据predict.id:" + predict.getId() + ", status:" + status);
                } catch (Exception e) {
                    logger.error(" err:" + e);
                }
            });
        }

    }

    /**
     * 存储异常预测图片到指定路径
     * @param data data
     */
    void saveErrImg(byte[] data, int id) {
        String savePath = String.valueOf(confiDao.getConfi("Path").getParams().get("data"));
        FileImageOutputStream imageOutput = null;
        try {
            logger.info("存储异常预测图片到指定路径savePath:" + savePath);
            File dir = new File(savePath);
            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdir();
                logger.info("创建存储目录!" + dir);
            }
            File file = new File(savePath + "/" + id  + "_" + System.currentTimeMillis() + ".jpg");
            logger.info("存储异常图片：" + file);
            imageOutput = new FileImageOutputStream(file);
            imageOutput.write(data, 0, data.length);
            logger.info("异常图片存储完毕!");

            // 指定文件夹下文件的数量
            int fileCount = FileCountUtil.getFileCount(savePath);
            logger.info("压缩文件的数量fileCount:" + fileCount + ", 条件count:" + COUNT);
            if (fileCount >= COUNT) {
                logger.info("达到条件开始压缩文件!");
                // 压缩文件
                ZipFileCompress.compress(savePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Err.存储异常图片时发生错误! " + e);
        } finally {
            try {

                if (imageOutput != null) {
                    imageOutput.flush();
                }
                if (imageOutput != null) {
                    imageOutput.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void run() {
        logger.info("启动线程 - 存储异常图片并打包压缩任务!");
        Runnable runnable = () -> {
            try {
                compare();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Err." + e);
            }
        };
        ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, 0, 60, TimeUnit.DAYS);

    }
}
