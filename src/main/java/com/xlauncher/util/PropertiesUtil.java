package com.xlauncher.util;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;


/**
 * @author Administrator
 */
@Component
public class PropertiesUtil {

    /**
     * 读写properties文件的对象
     */
    private Properties properties;

    /**
     * 添加一个记录器
     */
    private static Logger logger = Logger.getLogger(PropertiesUtil.class);

    /**
     * 文件名
     */
    private String fileName;


    /**
     * 初始化构造函数
     */
    public PropertiesUtil() {
        properties = new Properties();
        this.fileName = checkIfExist();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"));
            properties.load(br);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断model.properties在target/classes下是否存在,如果不存在则创建文件
     * @return 返回文件名（包含路径）
     */
    private String checkIfExist() {
        String fileName = PropertiesUtil.class.getClassLoader().getResource("").getPath() + "model.properties";
        logger.info("[model.properties]: " + fileName);
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }

    /**
     * 得到对象值
     * @return String
     */
    public String getValue(String key) {
        return properties.getProperty(key);
    }

    /**
     * 得到资源路径
     * @return String
     */
    public String getPath() {
        return PropertiesUtil.class.getClassLoader().getResource("").getPath();
    }
}
