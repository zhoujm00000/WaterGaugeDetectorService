package com.xlauncher.service.impl;

import com.xlauncher.dao.ConfiDao;
import com.xlauncher.dao.PredictDao;
import com.xlauncher.entity.Predict;
import com.xlauncher.service.ConsumeService;
import com.xlauncher.service.LengthService;
import com.xlauncher.service.PredictService;
import com.xlauncher.util.*;
import com.xlauncher.util.watergaugedetector.Evaluator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOException;
import java.io.*;
import java.text.NumberFormat;
import java.util.*;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/18 0018
 * @Desc :预测信息实现类
 **/
@Service
public class PredictServiceImpl implements PredictService {
    private Predict predict;
    @Autowired
    private ConfiDao confiDao;
    @Autowired
    private LengthService lengthService;
    @Autowired
    private PredictDao predictDao;
    @Autowired
    private PropertiesUtil propertiesUtil;
    @Autowired
    private ConsumeService consumeService;
    /**
     * 预测返回值-1,对象检测阶段异常（无水尺异常）
     */
    private static final int EVAL_STATUS_1 = -1;
    /**
     * 预测返回值-2,水尺对象分类阶段异常或水尺分类为bad（水尺异常）
     */
    private static final int EVAL_STATUS_2 = -2;
    /**
     * 预测返回值-3,对象精确检测阶段异常（无水尺异常）
     */
    private static final int EVAL_STATUS_3 = -3;
    /**
     * 预测返回值-4,水尺刻度预测阶段异常（刻度异常）
     */
    private static final int EVAL_STATUS_4 = -4;
    private static Logger logger = Logger.getLogger(PredictServiceImpl.class);

    /**
     * 单条预测数据
     * @param id id
     * @return Predict
     */
    @Override
    public Predict getPredict(int id) {
        return predictDao.getPredict(id);
    }

    /**
     * 历史预测列表查询
     * @param upStartTime 查询开始时间
     * @param lowStartTime 查询结束时间
     * @param sid 编号
     * @param identifyDescription 状态描述
     * @param number 页码数
     * @return List
     */
    @Override
    public List<Predict> listPredict(String upStartTime, String lowStartTime, String sid, String identifyDescription, int number) {
        logger.info("历史预测列表查询listPredict:参数upStartTime:" + upStartTime + ", lowStartTime:" + lowStartTime + ", sid:" + sid + ", number" + number);
        int len = 10;
        if (lowStartTime.length() <= len) {
            lowStartTime = lowStartTime + "23:59:59";
        }
        List<Predict> predictList = predictDao.listPredict(upStartTime, lowStartTime, sid, identifyDescription, number);
        predictList.forEach(predict -> {
            predict.setCollectTime(predict.getCollectTime().substring(0,19));
        });
        return predictList;
    }

    /**
     * 历史预测列表查询count数用于分页
     * @param upStartTime 查询开始时间
     * @param lowStartTime 查询结束时间
     * @param sid 编号
     * @param identifyDescription 状态描述
     * @return int
     */
    @Override
    public int countPredict(String upStartTime, String lowStartTime, String sid, String identifyDescription) {
        return predictDao.countPredict(upStartTime, lowStartTime, sid, identifyDescription);
    }

    /**
     * 字符串转二进制
     * @param str 要转换的字符串
     * @return  转换后的二进制数组
     */
    private static byte[] hex2byte(String str) {

        // 字符串转二进制
        if (str == null) {
            return null;
        }
        str = str.trim();
        int len = str.length();
        if (len == 0 || len % 2 == 1) {
            return null;
        }
        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[i / 2] = (byte) Integer
                        .decode("0X" + str.substring(i, i + 2)).intValue();
            }
            return b;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 预测信息查重
     *
     * @param collectTime 时间
     * @return 存在返回true;不存在返回false。
     */
    private boolean checkPredictIfExist(String collectTime, String sid) {
        logger.info(" ._____check Predict If Exist:" + collectTime);
        return predictDao.predictExistence(collectTime, sid) != 0;
    }

    /**
     * 存储预测数据
     *
     * @param map 读取第三方数据库封装的MAP对象
     * @return int
     */
    @Override
    public int insertPredict(Map<String, Object> map) {
        int status = 0;
        String thresh = (String) confiDao.getConfi("Threshold").getParams().get("data");
        // 存储数据，判断是否已存在
        if (checkPredictIfExist(String.valueOf(map.get("CollectTime")), (String)map.get("SID"))) {
            System.out.println("已存在数据：" + map.get("SID"));
            logger.info("10___.已存在数据：" + map.get("SID") + ", collect_time:" + map.get("CollectTime"));
            return status;
        } else {
            try {
                predict = new Predict();
                predict.setChannel((Integer) map.get("Channel"));
                predict.setSid(String.valueOf(map.get("SID")));
                predict.setCollectTime(String.valueOf(map.get("CollectTime")));
                predict.setSource(hex2byte((String) map.get("ImgInfoString")));
                predict.setPicture(ImageUtil.thumbnail(hex2byte((String) map.get("ImgInfoString"))));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Err.predict" + e);
            }
            logger.info("10___.存储预测数据insertPredict:" + predict);


            // 调用模型，获取图片信息
            if (map.get("ImgInfoString") != null) {
                byte[] imgByte = hex2byte((String) map.get("ImgInfoString"));
                logger.info("11___.图片的尺寸信息：imgByte.length:" + imgByte.length);

                int eval;
                logger.info("12___.Evaluator create success...");
                int height = lengthService.getLength(predict.getSid(), predict.getChannel());
                logger.info("13___.length height..." + height);
                // 进行预测
                try {
                    eval = Evaluator.evaluate(imgByte, Float.parseFloat(thresh), height);
                    // 计算预测耗时
                    long nowStamp = 0;
                    logger.info("---eval." + eval);
                    if (eval == EVAL_STATUS_1) {
                        // 对象检测阶段异常
                        try {
                            predict.setStatus(1);
                            predict.setIdentifyDescription("无水尺异常");
                            nowStamp = System.currentTimeMillis();
                            logger.info("14___A.对象检测阶段异常, eval：" + eval);
                            status = predictDao.insertPredict(predict);
                        } catch (Exception e) {
                            logger.error("14___A.Err:" + e);
                        }
                    } else if (eval == EVAL_STATUS_2) {
                        // 水尺对象分类阶段异常或水尺分类为bad
                        try {
                            predict.setStatus(1);
                            predict.setIdentifyDescription("水尺异常");
                            nowStamp = System.currentTimeMillis();
                            logger.info("14___B.水尺对象分类阶段异常或水尺分类为bad, eval：" + eval);
                            status = predictDao.insertPredict(predict);
                        } catch (Exception e) {
                            logger.error("14___B.Err:" + e);
                        }
                    } else if (eval == EVAL_STATUS_3){
                        // 对象精确检测阶段异常
                        try {
                            predict.setStatus(1);
                            predict.setIdentifyDescription("无水尺异常");
                            nowStamp = System.currentTimeMillis();
                            logger.info("14___C.水尺刻度预测阶段异常, eval：" + eval);
                            status = predictDao.insertPredict(predict);
                        } catch (Exception e) {
                            logger.error("14___C.Err:" + e);
                        }
                    } else if (eval == EVAL_STATUS_4){
                        // 水尺刻度预测阶段异常
                        try {
                            predict.setStatus(1);
                            predict.setIdentifyDescription("刻度异常");
                            nowStamp = System.currentTimeMillis();
                            logger.info("14___D.水尺刻度预测阶段异常, eval：" + eval);
                            status = predictDao.insertPredict(predict);
                        } catch (Exception e) {
                            logger.error("14___D.Err:" + e);
                        }
                    } else {
                        try {
                            // 无遮挡物遮挡图片
                            predict.setStatus(0);
                            predict.setIdentify((float) eval);
                            predict.setIdentifyDescription("正常");
                            nowStamp = System.currentTimeMillis();
                            logger.info("14___E.无遮挡物遮挡数据图片, eval：" + eval);
                            status = predictDao.insertPredict(predict);
                        } catch (Exception e) {
                            logger.error("14___E.Err:" + e);
                        }
                    }
                    logger.info("15___.当前数据predict1：" + predict + ", update status1:" + status);
                    logger.info("15___.nowStamp." + nowStamp);
                    // 毫秒差
                    long differStamp = nowStamp - (long)map.get("startStamp");
                    // 转换成String
                    String consumeTime = String.valueOf(differStamp);
                    Map<String, Object> consumeMap = new HashMap<>(1);
                    consumeMap.put("startTime", map.get("startTime1"));
                    consumeMap.put("consumeTime", consumeTime);
                    logger.info("16___.insertConsume...");
                    // 存储耗时统计
                    consumeService.insertConsume(consumeMap);
                } catch (IIOException | NumberFormatException e) {
                    e.printStackTrace();
                    logger.error("evaluate.Err!" + e);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("C.Err!" + e);
                }

            } else {
                logger.error("图片资源不存在! ,predict:" + predict);
            }
        }
        logger.info(" D.return status:" + status);
        return status;
    }

    /**
     * 获取数据库最新时间作为查询的开始时间
     *
     * @return String
     */
    @Override
    public String getCollectTime1() {
        String topTime = predictDao.getCollectTime1();
        if (topTime != null) {
            topTime = topTime.substring(0,topTime.length()-2);
        }
        logger.info("___topTime." + topTime);
        return topTime;
    }

    /**
     * 获取图片
     * @param id id
     * @return int
     */
    @Override
    public byte[] getImgData(int id) {
        return this.predictDao.getImgData(id).getSource();
    }

    /**
     * 获取缩略图
     * @param id id
     * @return int
     */
    @Override
    public byte[] getPicData(int id) {
        return this.predictDao.getPicData(id).getPicture();
    }

    /**
     * 更新修改预测数据
     *
     * @param predict 实例对象
     * @return int
     */
    @Override
    public int updatePredict(Predict predict) {
        logger.info("更新预测数据updatePredict:" + predict);
        return predictDao.updatePredict(predict);
    }

    /**
     * 上传图片返回识别数据
     *
     * @param bytes 图片数组
     * @param maxHeight 水尺总长度
     * @return Map
     */
    @Override
    public Map<String, Object> getImage(MultipartFile bytes, int maxHeight) {
        Map<String, Object> map = new HashMap<>(1);
        String thresh = (String) confiDao.getConfi("Threshold").getParams().get("data");
        logger.info("上传图片并返回识别数据thresh：" + thresh + ", maxHeight：" + maxHeight);
        try {
            byte[] input = bytes.getBytes();

            String path = propertiesUtil.getPath();
            String detectorModelPath = path + propertiesUtil.getValue("detectorModelPath");
            String predictorModelPath = path + propertiesUtil.getValue("predictorModelPath");
            String classifierModelPath = path + propertiesUtil.getValue("classifierModelPath");
            String multiDetectorModelPath = path + propertiesUtil.getValue("multiDetectorModelPath");
            logger.info(" 1.detectorModelPath：" + detectorModelPath);
            logger.info(" 2.predictorModelPath：" + predictorModelPath);
            logger.info(" 3.classifierModelPath：" + classifierModelPath);
            logger.info(" 4.multiDetectorModelPath：" + multiDetectorModelPath);
            Evaluator evaluator = Evaluator.getInstance(detectorModelPath, classifierModelPath, multiDetectorModelPath, predictorModelPath);
            if (null == evaluator) {
                System.err.println("Load Evaluator model failed, exit.");
                logger.error("Load Evaluator model failed, exit.");
            }
            int eval;
            try {
                eval = Evaluator.evaluate(input, Float.parseFloat(thresh), maxHeight);
                if (eval == -1) {
                    map.put("status", eval);
                    map.put("ERR", "图片有遮挡无法识别!");
                } else {
                    map.put("status", eval);
                    map.put("Data", eval);
                }
            } catch (IIOException | NumberFormatException e) {
                e.printStackTrace();
                logger.error("getImage.Err!" + e);
                map.put("status", -1);
                map.put("ERR", e.toString().split(":")[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Err." + e);
        } finally {
            System.out.println("this is finally!");
        }
        return map;
    }

    /**
     * 统计历史所有/最近正常、异常数据占比
     *
     * @return Map
     */
    @Override
    public Map<String, Object> countErrPredict() {
        logger.info("统计历史所有/最近5天，正常、异常数据占比");
        Map<String, Object> map = new HashMap<>(1);

        int allNormal = predictDao.countPredictByStatus(0);
        int allAbnormal = predictDao.countPredictByStatus(1);
        String allPercent = format((float)allAbnormal, (float)(allAbnormal + allNormal));
        map.put("allNormal", allNormal);
        map.put("allAbnormal", allAbnormal);
        map.put("allPercent", allPercent + "%");

        // 使用DATE_SUB(CURDATE(), INTERVAL #{num} DAY)函数，代表查询num+1天数据
        int normal = predictDao.countPredictByStatusNum(0,4);
        int abnormal = predictDao.countPredictByStatusNum(1,4);
        String percent = format((float)abnormal, (float)(abnormal + normal));
        map.put("normal", normal);
        map.put("abnormal", abnormal);
        map.put("percent", percent + "%");
        return map;
    }

    /**
     * 格式化计算
     *
     * @param arg1 arg1
     * @param arg2 arg2
     * @return String
     */
    private String format(float arg1, float arg2) {
        // 创建一个数值格式化的对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点2位
        numberFormat.setMaximumFractionDigits(2);
        return numberFormat.format(arg1 / arg2 * 100);
    }

    /**
     * 预测正确性统计
     *
     * @return Map
     */
    @Override
    public Map<String, Object> countCheckPredict() {
        logger.info("预测数据正确性统计");
        Map<String, Object> map = new HashMap<>(1);
        int error = predictDao.countPredictByCheck();
        int all = predictDao.countPredictTrue();
        if (all == 0) {
            map.put("percent", "0");
            map.put("remain", "0");
            return map;
        }
        String percent = format((float)all-error, (float)all);
        String remain = format((float)error, (float)all);
        map.put("correct", all-error);
        map.put("all", all);
        map.put("percent", percent + "%");
        map.put("remain", remain + "%");
        return map;
    }


}
