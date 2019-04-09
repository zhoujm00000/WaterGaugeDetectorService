package com.xlauncher.util;

import org.apache.log4j.Logger;

import java.io.File;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/19 0019
 * @Desc :计算存储文件夹下异常图片的数量以及删除该目录下的文件
 **/
public class FileCountUtil {
    private static Logger logger = Logger.getLogger(FileCountUtil.class);
    /**
     * 得到文件夹下文件的数量
     * @param path 路径
     * @return int
     */
     static int getFileCount(String path) {
        int fileCount = 0;
        File file = new File(path);
        File[] list = file.listFiles();
        if (list != null) {
            for (File aList : list) {
                logger.info("___aList." + aList);
                if (aList.isFile()) {
                    fileCount++;
                }
            }
        }
        return fileCount;
    }

    /**
     * 删除文件夹下所有文件不包括文件夹
     *
     * @param filePath 文件夹完整绝对路径
     */
     static void deleteAllFile(String filePath) {
         logger.info("__deleteAllFile filePath:" + filePath);
         File file = new File(filePath);
         if (!file.exists()) {
             return;
         }
         if (!file.isDirectory()) {
             return;
         }
         String[] tempList = file.list();
         logger.info("tempList:" + tempList.length);
         File temp;
         int num = 0;
         for (int i = 0; i <= tempList.length - 1; i++) {
             // 如果filePath不是以文件分隔符结尾，自动添加文件分隔符
             if (filePath.endsWith(File.separator)) {
                 temp = new File(filePath + tempList[i]);
             } else {
                 temp = new File(filePath + File.separator + tempList[i]);
             }
             if (temp.isFile()) {
                 logger.info(" a.temp.delete:" + temp);
                 temp.delete();
                 num++;
                 logger.info(" a.delete num:" + num);
             }
             if (temp.isDirectory()) {
                 logger.info(" b.temp.delete:" + temp);

             }
         }
     }

}
