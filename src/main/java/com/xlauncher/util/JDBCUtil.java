package com.xlauncher.util;

import com.xlauncher.dao.ConfiDao;
import com.xlauncher.entity.Predict;
import com.xlauncher.service.PredictService;
import com.xlauncher.service.impl.PredictServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.sql.*;
import java.util.*;


/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/19 0019
 * @Desc :封装JDBC调用第三方数据库、并反馈结果
 **/
@Component
public class JDBCUtil {
    @Autowired
    private ConfiDao confiDao;
    @Autowired
    private BatchUtil batchUtil;
    @Autowired
    private ThreadUtil threadUtil;
    @Autowired
    BackPredictUtil backPredictUtil;
    private static Connection con;
    private static Logger logger = Logger.getLogger(JDBCUtil.class);


    /**
     * 获取数据库连接
     *
     * @return Connection
     */
     synchronized Connection getConnection() throws Exception{
        Map<String, Object> mySQLMap = confiDao.getConfi("MySQL").getParams();

        String hostname = (String) mySQLMap.get("ip");
        String port = (String) mySQLMap.get("port");
        String user = (String) mySQLMap.get("name");
        String password = (String) mySQLMap.get("password");
        String database = (String) mySQLMap.get("database");
        String url = "jdbc:sqlserver://" + hostname + ":" + port + ";databaseName=" + database;
        logger.info(" 2___.WaterResource服务信息: server.hostname:" + hostname + " ,server.port:" + port
                + " ,server.user:" + user + " ,server.password:" + password
                + " ,url:" + url);

        try {
            // 1.加载驱动程序
            // Class.forName()加载一个类，返回的该类的类名
            // newInstance()方法可以创建一个Class对象的实例，使用该方法实例化一个类时该类必须已经被加载了
            // new关键字实例化一个类时先加载再实例化
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            DriverManager.setLoginTimeout(30);
            // 2.通过连接池创建Connection连接
            con = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Err." + e);
            throw new Exception("Err.Connection refused!" + e);
        }
        return con;
    }

    /**
     * 调用数据库
     */
    void server(int num) throws Exception {
        String startTime = threadUtil.getTimeStamp();
        if (startTime == null) {
            logger.warn("___.startTime is null!  return immediately!");
            return;
        }
        logger.info("3____.第[" + num + "]次执行定时任务!");
        logger.info("4____.startTime!" + startTime);

        int rowCount = getCount(startTime);
        logger.info("5___rowCount." + rowCount);
        // 实现分页, 每页20条
        if (rowCount !=0) {
            // 获取数据库连接
            Connection con = getConnection();
            Statement st = null;
            ResultSet rs = null;
            // 计算页数, 两数相除, 有余数就进一位
            int page = (rowCount%20==0)?rowCount/20:(rowCount/20+1);

            for (int n=1; n<=page +1;n++) {

                String sql = "select * from ImageInfo WHERE InsertTime > '" + startTime + "' Order by InsertTime Offset " + (n-1)*20 + " Row Fetch Next 20 Rows Only";
                logger.info("6___.读取第[" + n + "]页开始! 总共[" + page + "]页! THIS SQL IS." + sql);
                try {
                    // 获取Statement
                    st = con.createStatement();
                    // 执行查询操作
                    rs = st.executeQuery(sql);
                    int index = 0;
                    // 光标先后移动，判断是否存在下一个元素
                    while (rs.next()) {
                        Map<String, Object> map = new HashMap<>(1);
                        index ++;
                        // 计算当前时间
                        String startTime1 = DatetimeUtil.getDate(System.currentTimeMillis());
                        // 当前时间戳
                        long startStamp = Long.parseLong(DateUtil.dateToStamp(startTime1));

                        // 格式化采集时间
                        String stamp = DateUtil.dateToStamp(rs.getString("CollectTime").substring(0, 19));
                        String date = DateUtil.stampToDate(stamp);

                        logger.info("7___.WaterResource的数据信息index.>:"
                                + index
                                + ", SID: " + rs.getString("SID")
                                + ", ImgInfo: " + rs.getString("ImgInfo").length()
                                + ", CollectTime: " + rs.getString("CollectTime"));

                        map.put("Channel", rs.getInt("Channel"));
                        map.put("SID", rs.getString("SID"));
                        map.put("ImgInfoString", rs.getString("ImgInfo"));
                        map.put("CollectTime", date);
                        map.put("startStamp", startStamp);
                        map.put("startTime1", startTime1);
                        logger.info("8___.执行存储预测!");
                        try {
                            batchUtil.insertPredict(map);
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error("---eRR." + e);
                        }
                        logger.info("9___.预测结束!");
                    }
                    logger.info("20___.读取第[" + n + "]页结束! THIS SQL IS." + sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                    logger.error("____.Server Err." + e);
                }
                try {
                    if (st != null) {
                        st.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    logger.error("server.Err.release!" + e);
                }
            }
        }
        backPredictUtil.batchBack(startTime);
        logger.info("A--------------------------第["+ num +"]次定时任务执行完毕--------------------------A.");
    }

    /**
     * 获取数据总数用于分页
     *
     * @param startTime 获取数据的开始时间
     * @return count数
     */
    private int getCount(String startTime) throws Exception {
        int rowCount = 0;
        // 获取数据库连接
        Connection con = getConnection();
        Statement st = null;
        ResultSet rs = null;
        String count = "select count(*) from ImageInfo WHERE InsertTime > ' " + startTime + "'";
        try {
            // 获取Statement
            st = con.createStatement();
            // 执行查询操作
            rs = st.executeQuery(count);

            // 光标先后移动，判断是否存在下一个元素
            while (rs.next()) {
                rowCount=rs.getInt(1);
            }
            logger.info("获取数据总数用于分页.rowCount:" + rowCount + ", SQL." + count);
            System.out.println("获取数据总数用于分页.rowCount:" + rowCount + ", SQL." + count);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getCount.Err." +e);
        }
        try {
            release(rs,st,con);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("getCount.release.Err." +e);
        }
        return rowCount;
    }

    /**
     * 将预测值存入数据库
     *
     * @param predict predict 预测实体对象
     */
    void backServer(Predict predict) throws Exception {

        float identify = predict.getIdentify();
        Integer  sta = predict.getStatus();
        String  sid = predict.getSid();
        String  collectTime = predict.getCollectTime().substring(0,19);
        Integer  channel = predict.getChannel();
        logger.info("18.____backServer.predict:" + predict);
        // 获取数据库连接
        Connection con = getConnection();
        PreparedStatement ps = null;
        String sql = "update ImageInfo set Identificationdata=?, Occlusionsignal=? where SID=? and CollectTime=? and Channel=?";
        int status;
        try {
            ps = con.prepareStatement(sql);
            ps.setFloat(1,identify);
            ps.setInt(2,sta);
            ps.setString(3,sid);
            ps.setString(4,collectTime);
            ps.setInt(5,channel);
            status = ps.executeUpdate();
            logger.info("19.____.backServer status:" + status + ", SQL:" + sql);
            System.out.println(" ____.backServer status:" + status + ", SQL:" + sql);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("backServer.Err." + e);
        } finally {
            if (ps != null) {
                ps.close();
            }
            con.close();
        }
    }

    /**
     * 获取第三方数据库最新的采集时间
     *
     * @return 最新的采集时间
     */
    String getTop1CollectTime() throws Exception {
        // 获取数据库连接
        Connection con = getConnection();
        String topCollectTime=null;
        Statement st;
        ResultSet rs;
        String topCollectTimeSQL = "select top (1) InsertTime from ImageInfo ORDER BY InsertTime DESC";
        // 获取Statement
        st = con.createStatement();
        // 执行查询操作
        rs = st.executeQuery(topCollectTimeSQL);
        // 光标先后移动，判断是否存在下一个元素
        while (rs.next()) {
            // 标准化时间格式(yyyy-MM-dd hh:mm:ss)
            topCollectTime = rs.getString("CollectTime");
            topCollectTime = topCollectTime.substring(0,topCollectTime.length()-2);
        }
        try {
            release(rs,st,con);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("getTop1CollectTime.Err." + e);
        }
        return topCollectTime;
    }

    /**
     * 获取第三方数据库最老的采集时间
     *
     * @return 最老的采集时间
     */
    String getFirst1CollectTime() throws Exception {
        // 获取数据库连接
        Connection con = getConnection();
        String firstCollectTime=null;
        Statement statement;
        ResultSet resultSet;
        String topCollectTimeSQL = "select top  (1) InsertTime from ImageInfo ORDER BY InsertTime ASC";
        // 获取Statement
        statement = con.createStatement();
        // 执行查询操作
        resultSet = statement.executeQuery(topCollectTimeSQL);
        // 光标先后移动，判断是否存在下一个元素
        while (resultSet.next()) {
            // 标准化时间格式(yyyy-MM-dd hh:mm:ss)
            firstCollectTime = resultSet.getString("CollectTime");
            firstCollectTime = firstCollectTime.substring(0,firstCollectTime.length()-2);
        }
        try {
            release(resultSet,statement,con);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("getFirst1CollectTime.Err." + e);
        }
        return firstCollectTime;
    }

    /**
     * 获取第三方数据库最新的图片数据
     */
    int getAllImageInfo() throws Exception {
        int result = 0;
        // 获取数据库连接
        Connection con = getConnection();
        Statement st = null;
        ResultSet rs = null ;
        String getAllImageInfoSQL = "select * from ImageInfo";
        try {
            // 获取Statement
            st = con.createStatement();
            // 执行查询操作
            rs = st.executeQuery(getAllImageInfoSQL);
            int rowCount=0;
            // 光标先后移动，判断是否存在下一个元素
            while (rs.next()) {
                rowCount++;
            }
            // 一次创建一次关闭释放资源
            st.close();
            rs.close();
            // 实现分页, 每页20条
            if (rowCount !=0) {
                // 计算页数, 两数相除, 有余数就进一位
                int page = (rowCount%20==0)?rowCount/20:(rowCount/20+1);
                result = 1;
                for (int n=1; n<=page +1;n++) {
                    String sql = "select * from ImageInfo ORDER BY InsertTime Offset " + (n-1)*20 + " Row Fetch Next 20 Rows Only";
                    logger.info("读取第[" + n + "]页开始! 总共[" + page + "]页! THIS SQL IS." + sql);
                    try {
                        // 获取Statement
                        st = con.createStatement();
                        // 执行查询操作
                        rs = st.executeQuery(sql);
                        // 光标先后移动，判断是否存在下一个元素
                        while (rs.next()) {
                            Map<String, Object> map = new HashMap<>(1);
                            // 计算当前时间
                            String startTime1 = DatetimeUtil.getDate(System.currentTimeMillis());
                            // 当前时间戳
                            long startStamp = Long.parseLong(DateUtil.dateToStamp(startTime1));

                            // 格式化采集时间
                            String stamp = DateUtil.dateToStamp(rs.getString("CollectTime").substring(0, 19));
                            String date = DateUtil.stampToDate(stamp);

                            map.put("Channel", rs.getInt("Channel"));
                            map.put("SID", rs.getString("SID"));
                            map.put("ImgInfoString", rs.getString("ImgInfo"));
                            map.put("CollectTime", date);
                            map.put("startStamp", startStamp);
                            map.put("startTime1", startTime1);
                            batchUtil.insertPredict(map);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        logger.error("___Err." + e);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getAllImageInfo.Err" + e);
        }
        release(rs,st,con);
        return result;
    }

    /**
     * 确认配置
     *
     * @param map
     * @return
     */
    public synchronized int confirm(Map<String, Object> map) throws Exception {
        int confirm = 0;
        String hostname = (String) map.get("ip");
        String port = (String) map.get("port");
        String user = (String) map.get("name");
        String password = (String) map.get("password");
        String database = (String) map.get("database");
        String url = "jdbc:sqlserver://" + hostname + "\\sqlexpress:" + port + ";databaseName=" + database;
        logger.info("url." + url + ", user." + user + ", password." + password);
        try {
            // 1.加载驱动程序
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            DriverManager.setLoginTimeout(30);
            // 2.通过连接池创建Connection连接
            con = DriverManager.getConnection(url,user,password);
            if (con != null) {
                confirm = 1;
                con.close();
            }
        } catch (InstantiationException
                | IllegalAccessException
                | ClassNotFoundException
                | SQLException e) {
            e.printStackTrace();
            logger.error("Err." + e);
            throw new Exception("Err.Connection refused!");
        }
        return confirm;
    }

    /**
     * 释放连接
     *
     * @param rs ResultSet
     * @param st Statement
     * @param con Connection
     */
    private synchronized static void release(ResultSet rs, Statement st, Connection con) throws SQLException{
        try {
            if(rs != null) {
                rs.close();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            logger.error("rs.close ERR!" + e1);
        }
        try {
            if(st != null) {
                st.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("st.close ERR!" + e);
        } finally {
            if(con != null) {
                con.close();
            }
        }
    }

    void unRegister() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void getImg() throws Exception {
        // 获取数据库连接
        Connection con = getConnection();

        Statement st = null;
        ResultSet rs = null ;
        int count = 0;
//        String sql = "select * from ImageInfo WHERE CollectTime = '2018-11-28 10:36:00' and SID = 4726510156";
        String sql = "select * from ImageInfo WHERE InsertTime between '2019-03-13 00:00:00' and '2019-03-13 23:59:00'";
        try {
            // 获取Statement
            st = con.createStatement();
            // 执行查询操作
            rs = st.executeQuery(sql);
            // 光标先后移动，判断是否存在下一个元素
            while (rs.next()) {
                byte[] data = rs.getBytes("ImgInfo");
                String[] names = rs.getString("CollectTime").substring(0,19).split(" ");
                String[] times = names[1].split(":");
                String name = names[0] + "_" + times[0] + "-" + times[1] + "-" + times[2];
                File file = new File("D:\\images" + "\\" + name + ".jpg");
                FileImageOutputStream imageOutput = new FileImageOutputStream(file);
                imageOutput.write(data, 0, data.length);
                imageOutput.flush();
                imageOutput.close();
                count++;
                System.out.println("count_" + count + ".time_" + rs.getString("CollectTime"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        release(rs,st,con);
    }

//    /**
//     * 调用数据库
//     */
//    public void server(long arg, int num) {
//
//        arg = arg * 60 * 1000;
//        for (int i = 29; i >= 0; i--) {
//            logger.info(">>>>>>>>>>>>>>>>>>>i" + i);
//            String[] hour = {" 00"," 01"," 02"," 03"," 04"," 05"," 06"," 07"," 08"," 09"," 10"," 11"," 12"," 13"," 14"," 15"," 16"," 17"," 18"," 19"," 20"," 21"," 22"," 23"};
//            logger.info("length." + hour.length);
//            for (int j=0; j<=hour.length-1; j++) {
//
//                logger.info(">>>>>>>>>>>>>>>>>>>j" + j + hour[j]);
//                List<Map<String, Object>> mapList = new ArrayList<>(1);
//                // 获取数据库连接
//                Connection con = null;
//                try {
//                    con = getConnection();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    logger.error("con Err." + e);
//                }
//                Statement st = null;
//                ResultSet rs = null;
//
//                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Calendar c = Calendar.getInstance();
//
//                c.setTime(new Date());
//                c.add(Calendar.DATE, -i);
//                Date d = c.getTime();
//
//
//                String startTime = null;
//                try {
//                    startTime = format.format(d).substring(0,10);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    logger.error("startTime Err." + e);
//                }
//                String lastTime = startTime;
//
//                lastTime += hour[j] +":00:00";
//                startTime += hour[j] +":59:59";
//
//                logger.info("_____________________>");
//                logger.info("___lastTime." +lastTime);
//                logger.info("___startTime." +startTime);
//                //        long startStamp = System.currentTimeMillis();
//                //        String lastTime = DateUtil.stampToDate(String.valueOf(startStamp-arg));
//                //        String startTime = DateUtil.stampToDate(String.valueOf(startStamp));
//                String sql = "select * from ImageInfo WHERE CollectTime BETWEEN ' " + lastTime + " ' and ' " + startTime + "'";
//                //        String sql = "select top "+ 300*num + " * from ImageInfo ";
//                try {
//                    logger.info(" 3.WaterResource SQL:" + sql);
//                    // 获取Statement
//                    st = con.createStatement();
//                    // 执行查询操作
//                    rs = st.executeQuery(sql);
//                    // 光标先后移动，判断是否存在下一个元素
//                    while (rs.next()) {
//                        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        Calendar c1 = Calendar.getInstance();
//
//                        c1.setTime(new Date());
//                        c1.add(Calendar.DATE, -i);
//                        Date d1 = c1.getTime();
//                        String startTime1 = format1.format(d1);
//                        long startStamp = 0;
//                        try {
//                            startStamp = Long.parseLong(DateUtil.dateToStamp(format.format(d1)));
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                            logger.error("startStamp Err." + e);
//                        }
//                        Map<String, Object> map = new HashMap<>(1);
//                        String stamp;
//                        String date = null;
//                        try {
//                            stamp = DateUtil.dateToStamp(rs.getString("CollectTime").substring(0, 19));
//                            date = DateUtil.stampToDate(stamp);
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                            logger.error(" 1.Err." + e);
//                        }
//                        logger.info(" 4.WaterResource的数据信息:Channel:" + rs.getInt("Channel")
//                                + ",SID: " + rs.getString("SID")
//                                + ",ImgInfo: " + rs.getString("ImgInfo").length()
//                                + ",ReportMode: " + rs.getString("ReportMode")
//                                + ",CollectTime: " + rs.getString("CollectTime"));
//
//                        map.put("Channel", rs.getInt("Channel"));
//                        map.put("SID", rs.getString("SID"));
//                        map.put("ImgInfoString", rs.getString("ImgInfo"));
//                        map.put("CollectTime", date);
//                        map.put("startStamp", startStamp);
//                        map.put("startTime1", startTime1);
//                        map.put("i",i);
//                        predictService.insertPredict(map);
//                        logger.info("-----------------------i");
////                    logger.info(" 5.WaterResource存储mapList");
////                    mapList.add(map);
////                    logger.info("mapList.size:" + mapList.size());
//                    }
//                    logger.info(" 6.第 " + num + " 轮数据读取完毕..." + sql);
//                    //            long nowStamp = System.currentTimeMillis();
//                    //            long differStamp = nowStamp - startStamp;
//                    //            // 转换成String
//                    //            String consumeTime = String.valueOf(differStamp);
//                    //            Map<String, Object> consumeMap = new HashMap<>(1);
//                    //            consumeMap.put("startTime", startTime);
//                    //            consumeMap.put("consumeTime", consumeTime);
//                    //            logger.info(" 7.存储读取第 " + num +" 轮数据de消耗时间：" + consumeMap);
//                    //            consumeService.insertConsume(consumeMap);
//                    logger.info(" ==================================================-C ");
////                batchUtil.addPredict(mapList);
//                    logger.info(">>>>>>>>>>>>>>>>");
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                    logger.error(" 2.Err." + e);
//                } finally {
//                    try {
//                        logger.info("____release...");
//                        release(rs, st, con);
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                        logger.error(" 3.Err." + e);
//                    }
//                }
//            }
//
//        }
//
//    }

}
