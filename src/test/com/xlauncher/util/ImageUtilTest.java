package com.xlauncher.util;

import com.xlauncher.dao.PredictDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * @Auther :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/11/2 0002
 * @Desc :
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml","classpath:spring-*.xml"})
@Transactional
public class ImageUtilTest {
    @Autowired
    PredictDao predictDao;
    @Test
    public void test1() throws Exception {
        byte[] bytes = predictDao.getPredict(5515).getSource();
        ImageUtil.thumbnail(bytes);
    }
    @Test
    public void time() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        System.out.println(System.currentTimeMillis());
        System.out.println(DateUtil.stampToDate("153932911100"));
        System.out.println("---");
        System.out.println(DateUtil.dateToStamp("2018-10-12 00:59:59"));
//        for (int i=0; i<30; i++) {
//            //过去七天
//            c.setTime(new Date());
//            c.add(Calendar.DATE, - i);
//            Date d = c.getTime();
//            String day = format.format(d);
//            System.out.println("过去" + i + "天：" + day);
//            System.out.println(DateUtil.dateToStamp(day));
//            System.out.println(DateUtil.stampToDate(DateUtil.dateToStamp(day)));
////            System.out.println(i + "i" +DateUtil.stampToDate(String.valueOf(System.currentTimeMillis()-86400000*(i))));
//        }

    }
    @Test
    public void math() {
        int a= 40;
        int b=20;
        int result = (a%b==0)?a/b:(a/b+1);
        System.out.println("---" + result);
    }

}