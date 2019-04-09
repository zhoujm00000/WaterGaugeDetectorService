package com.xlauncher.util;

import com.xlauncher.service.ConsumeService;
import com.xlauncher.service.LengthService;
import com.xlauncher.service.PredictService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/31 0031
 * @Desc :批量存储预测数据
 **/
@Component
public class BatchUtil {
    @Value("${status}")
    private String status;
    private static final String IS_STORE = "1";
    @Autowired
    private  PredictService predictService;
    @Autowired
    private  ConsumeService consumeService;
    @Autowired
    private  LengthService lengthService;
    private static Logger logger = Logger.getLogger(JDBCUtil.class);


    /**
     * 存储预测信息,可配置存储预测数据, status=0 存储所有预测数据; status=1 存储已配置预测数据
     *
     * @param map
     */
    void insertPredict(Map<String, Object> map) {
        String sid = (String) map.get("SID");
        int channel = (int) map.get("Channel");
        logger.info("___存储预测信息,可配置存储预测数据status." + status);
        if (status.equals(IS_STORE)) {
            int height = lengthService.getLength(sid, channel);
            if (height != 0) {
                predictService.insertPredict(map);
            } else {
                logger.warn("[" + sid + "] 没有进行水尺总长配置!");
            }
        } else {
            predictService.insertPredict(map);
        }
    }

    /**
     * 批量存储预测数据
     *
     * @param mapList mapList
     */
    void addPredict(List<Map<String, Object>> mapList) {
        final long[] startStamp = new long[1];
        final String[] startTime = new String[1];
        final int[] i = new int[1];
        logger.info(" 8.BatchUtil - addPredict 批量存储：size." + mapList.size());
        mapList.forEach(map -> {
            logger.info(" 8*.map CollectTime:" + map.get("CollectTime"));
            startStamp[0] = (long) map.get("startStamp");
            startTime[0] = (String) map.get("startTime");
            i[0] = (int) map.get("i");
            System.out.println("CollectTime:" + map.get("CollectTime"));
            predictService.insertPredict(map);
        });

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -i[0]);
        Date d = c.getTime();

        long nowStamp = 0;
        try {
            nowStamp = Long.parseLong(DateUtil.dateToStamp(format.format(d)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long differStamp = nowStamp - startStamp[0];
        logger.info("_______________________<");
        logger.info("___i." + i[0]);
        logger.info("___startStamp." + startStamp[0]);
        logger.info("___startTime." + startTime[0]);
        logger.info("___nowStamp." + nowStamp);
        logger.info("___differStamp." + differStamp);
        // 转换成String
        String consumeTime = String.valueOf(differStamp);
        Map<String, Object> consumeMap = new HashMap<>(1);
        consumeMap.put("startTime", startTime[0]);
        consumeMap.put("consumeTime", consumeTime);
        consumeService.insertConsume(consumeMap);
        System.out.println("===");
        logger.info(" ____消耗时间：" + consumeMap);
        logger.info(" ==================================================-C ");
    }
}
