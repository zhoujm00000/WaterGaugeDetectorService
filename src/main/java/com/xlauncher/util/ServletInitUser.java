package com.xlauncher.util;

import com.xlauncher.dao.ConfiDao;
import com.xlauncher.dao.UserDao;
import com.xlauncher.entity.Configuration;
import com.xlauncher.entity.User;
import com.xlauncher.util.watergaugedetector.Evaluator;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author baisl
 */
public class ServletInitUser implements ServletContextListener {

    private Logger logger = Logger.getLogger(ServletInitUser.class);
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.info("***ServletInitUser-contextInitialized***" + servletContextEvent.toString());
        // 获取容器和相关的bean
        UserDao userDao = (UserDao) SpringContextUtil.getBean("userDao");
        ConfiDao confiDao = (ConfiDao) SpringContextUtil.getBean("confiDao");
        ThreadUtil threadUtil = (ThreadUtil) SpringContextUtil.getBean("threadUtil");
        SaveErrImgUtil saveErrImgUtil = (SaveErrImgUtil) SpringContextUtil.getBean("saveErrImgUtil");
        logger.info("TOMCAT初始化 - 获取容器和相关的bean：");
        User user = userDao.getUserByAccount("admin");
        if (user != null) {
            logger.info("已存在admin用户：" + user);
        } else {
            User initUser = new User();
            initUser.setAccount("admin");
            initUser.setPassword(MD5Util.getResult("111111"));
            userDao.initUser(initUser);
            logger.info("初始化admin用户：" + initUser);
        }
        logger.info("TOMCAT初始化 - 验证MySQL服务是否可用：");
        String initIp = "127.0.0.1";
        Configuration configuration = confiDao.getConfi("MySQL");
        String hostname = String.valueOf(configuration.getParams().get("ip"));
        String password = String.valueOf(configuration.getParams().get("password"));
        String name = String.valueOf(configuration.getParams().get("name"));
        String port = String.valueOf(configuration.getParams().get("port"));
        String database = String.valueOf(configuration.getParams().get("database"));
        String url = "jdbc:sqlserver://" + hostname + ":" + port + ";databaseName=" + database;
        try {
            // 默认ip为127.0.0.1
            if (!initIp.equals(hostname)) {
                // 1.加载驱动程序
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
                DriverManager.setLoginTimeout(30);
                // 2.通过连接池创建Connection连接
                Connection con = DriverManager.getConnection(url,name,password);
                if (con != null) {
                    new EvaluatorGetInstance();
                    // 系统启动时启动定时任务
                    threadUtil.timeTask();
                    // 存储异常图片任务
                    saveErrImgUtil.run();
                    con.close();
                    logger.info("TOMCAT初始化 - 启动线程threadUtil.run：");
                } else {
                    logger.warn("TOMCAT初始化 - 服务连接失败!");
                }
            } else {
                logger.warn("TOMCAT初始化 - 服务没有配置!");
            }
        } catch (InstantiationException
                | IllegalAccessException
                | ClassNotFoundException
                | SQLException e) {
            e.printStackTrace();
            logger.error("Err." + e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        logger.info("***ServletInitUser-contextDestroyed***" + servletContextEvent.toString());
        logger.warn("TOMCAT初始化 - 注销!");
        JDBCUtil jdbcUtil = (JDBCUtil) SpringContextUtil.getBean("jdbcUtil");
        // 系统结束关闭con连接
        jdbcUtil.unRegister();
    }
}

/**
 * 实例化模型
 */
class EvaluatorGetInstance{
    private static Logger logger = Logger.getLogger(EvaluatorGetInstance.class);

    static{
        EvaluatorGetInstance.getInstance();
    }

    /**
     * 实例化加载模型
     */
    private static void getInstance(){
        PropertiesUtil propertiesUtil = (PropertiesUtil) SpringContextUtil.getBean("propertiesUtil");
        // 三个模型路径
        String path = propertiesUtil.getPath();
        String detectorModelPath = path + propertiesUtil.getValue("detectorModelPath");
        String predictorModelPath = path + propertiesUtil.getValue("predictorModelPath");
        String classifierModelPath = path + propertiesUtil.getValue("classifierModelPath");
        String multiDetectorModelPath = path + propertiesUtil.getValue("multiDetectorModelPath");
        logger.info("TOMCAT初始化 - 1.detectorModelPath：" + detectorModelPath);
        logger.info("TOMCAT初始化 - 2.predictorModelPath：" + predictorModelPath);
        logger.info("TOMCAT初始化 - 3.classifierModelPath：" + classifierModelPath);
        logger.info("TOMCAT初始化 - 4.multiDetectorModelPath：" + multiDetectorModelPath);
        // 实例化模型
        Evaluator evaluator = Evaluator.getInstance(detectorModelPath, classifierModelPath, multiDetectorModelPath, predictorModelPath);
        if (null == evaluator) {
            System.err.println("Load Evaluator model failed, exit!");
        }
        logger.info("通过初始化构造器实例化加载模型!");
    }
}
