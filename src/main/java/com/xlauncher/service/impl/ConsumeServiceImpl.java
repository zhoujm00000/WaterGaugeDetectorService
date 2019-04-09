package com.xlauncher.service.impl;

import com.xlauncher.dao.ConsumeDao;
import com.xlauncher.entity.Consume;
import com.xlauncher.service.ConsumeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/17 0017
 * @Desc :耗时统计实现类
 **/
@Service
public class ConsumeServiceImpl implements ConsumeService {
    private final ConsumeDao consumeDao;
    private static Logger logger = Logger.getLogger(ConsumeServiceImpl.class);

    @Autowired
    public ConsumeServiceImpl(ConsumeDao consumeDao) {
        this.consumeDao = consumeDao;
    }

    /**
     * 存储一轮数据读取消耗时间
     * @param objectMap 实例对象
     * @return int
     */
    @Override
    public int insertConsume(Map<String, Object> objectMap) {
        logger.info("存储读取一轮数据的消耗时间insertConsume:" + objectMap);
        int status = 0;
        try {
            Consume consume = new Consume();
            consume.setStartTime((String) objectMap.get("startTime"));
            consume.setConsumeTime((String) objectMap.get("consumeTime"));
            status = consumeDao.insertConsume(consume);
            int count = consumeDao.countMonth();
            logger.info("___status." + status + ",___count." + count);
            if (count != 0) {
                consumeDao.emptyMonth();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    /**
     * 每30天平均一轮数据读取消耗时间
     * @return Map
     */
    @Override
    public Map<String, Object> avgConsumeTimeByDay() {
        int monthSub = 30;
        SimpleDateFormat dateFm = new SimpleDateFormat("dd");
        Map<String, Object> map = new HashMap<>(1);
        List<String> listDay = new ArrayList<>(1);
        List<String> listAvg = new ArrayList<>(1);
        List<String> listMax = new ArrayList<>(1);
        List<String> listMin = new ArrayList<>(1);
        for (int i = 0; i < monthSub; i++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -i);
            Date dateMonth = cal.getTime();
            String day = dateFm.format(dateMonth);
            String avg = this.consumeDao.avgConsumeTimeByDay(-i);
            String max = this.consumeDao.maxConsumeTimeByDay(-i);
            String min = this.consumeDao.minConsumeTimeByDay(-i);
            listDay.add(day);
            listAvg.add(avg);
            listMax.add(max);
            listMin.add(min);
            // 将集合中value为null的替换成"0"
            Collections.replaceAll(listAvg, null, "0");
            Collections.replaceAll(listMax, null, "0");
            Collections.replaceAll(listMin, null, "0");
        }
        // 反转list
        Collections.reverse(listDay);
        Collections.reverse(listAvg);
        Collections.reverse(listMax);
        Collections.reverse(listMin);
        map.put("day", listDay);
        map.put("avg", listAvg);
        map.put("max", listMax);
        map.put("min", listMin);
        logger.info("统计每30天平均一轮数据读取消耗时间avgConsumeTimeByDay:" + map);
        return map;
    }
}
