package com.xlauncher.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 时间转换工具类
 * @author zhangxiaolong
 * @date 2018-02-12
 */
public class DatetimeUtil {

    private final static String datetimeFormat = "yyyy-MM-dd HH:mm:ss";

    private final static String dateFormat = "yyyy-MM-dd";
    /**
     * 得到java.sql.Date
     *
     * @param strDate String type
     * @return java.sql.Date object
     * @throws ParseException parseException
     */
    public static Date getDate(String strDate) {
        Date date = null;
        try {
            date = new Date(new SimpleDateFormat(dateFormat).parse(strDate).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 得到java.sql.Timestamp
     *
     * @param strDate Date of String type
     * @return java.sql.Timestamp object
     * @throws ParseException Date format Exception
     */
    public static Timestamp getTimestamp(String strDate) {
        Timestamp timestamp = null;
        try {
            timestamp = new Timestamp(new SimpleDateFormat(datetimeFormat).parse(strDate).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    public static String getDate(long strDate){
        SimpleDateFormat format =  new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date = new java.util.Date();
        return format.format(date.getTime());

    }

    /**
     * 得到String via Timestamp
     *
     * @param timestamp java.sql.Timestamp object
     * @return Format datetime string
     */
    public static String stampToDate(String timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (timestamp == null) {
            timestamp = String.valueOf(System.currentTimeMillis());
        }
        return simpleDateFormat.format(new Date(Long.parseLong(String.valueOf(timestamp))));
    }

    /**
     * 得到format date string via date yyyy-MM-dd
     *
     * @param date java.sql.Date
     * @return Format date string
     */
    public static String getFormateDate(Date date) {
        return new SimpleDateFormat(dateFormat).format(date);
    }
}
