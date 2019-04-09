package com.xlauncher.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/18 0018
 * @Desc :压缩文件
 **/
public class ZipFileCompress {

    private static Logger logger = Logger.getLogger(ZipFileCompress.class);

    /**
     * 压缩指定文件
     * @param path 指定路径，压缩目录，可以指向一个文件
     */
    static void compress(String path) {
        // 初始化支持多级目录压缩的ZipMultDirectoryCompress()
        ZipFileCompress zipCompress = new ZipFileCompress();
        logger.info("___compress path:" + path);
        // 压缩文件路径
        String destPath = path + "/compress";
        logger.info("___compress destPath:" + destPath);
        // 压缩文件
        String destFile =  "compress_" + DatetimeUtil.getDate(DatetimeUtil.getDate(System.currentTimeMillis())) + ".zip";
        logger.info("___compress destFile:" + destFile);
        // 默认的相对地址，为根路径
        String defaultParentPath = "";
        ZipOutputStream zos = null;
        try {
            File file = new File(destPath);
            if (!file.exists()) {
                file.mkdir();
                logger.info("创建压缩文件路径destPath:" + destPath);
            }
            // 创建一个Zip输出流
            zos = new ZipOutputStream(new FileOutputStream(destPath + "/" + destFile));
            // 启动压缩进程
            zipCompress.startCompress(zos, defaultParentPath, path);
            logger.info("--------------------L");
            // 删除文件
            FileCountUtil.deleteAllFile(path);

        } catch (FileNotFoundException e) {
            logger.error("FileNotFoundException:" + e);
        } finally {
            try {
                if(zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("IOException:" + e);
            }
        }
    }

    /**
     * 开始压缩
     *
     * @param zos
     * @param oppositePath
     * @param directory
     */
    private void startCompress(ZipOutputStream zos, String oppositePath, String directory) {
        logger.info("___startCompress directory:" + directory);
        File file = new File(directory);
        if (file.isDirectory()) {
            // 如果是压缩目录
            File[] files = file.listFiles();
            logger.info("___files:" + files.length);
            for (File aFile : files) {
                logger.info("___需要压缩的afile." + aFile);
                if (aFile.isDirectory()) {
                    logger.info("___aFile.isDirectory()");
                } else {
                    logger.info("___compressFile start.");
                    // 如果不是目录，则进行压缩
                    compressFile(zos, oppositePath, aFile);
                }
            }
        } else {
            // 如果是压缩文件，则直接调用压缩方法进行压缩
            compressFile(zos,oppositePath,file);
        }
    }

    /**
     * 压缩文件
     *
     * @param zos
     * @param oppositePath
     * @param file
     */
    private void compressFile(ZipOutputStream zos, String oppositePath, File file) {
        // 创建一个zip条目，每个zip条目都是必须相对路径
        ZipEntry entry = new ZipEntry(oppositePath + file.getName());
        InputStream is = null;
        logger.info("___compressFile path:" + oppositePath + ", file:" + file);
        try {
            // 将条目保存到zip压缩文件中
            zos.putNextEntry(entry);
            // 从文件输入流中读取数据，并将数据写到输出流中
            is = new FileInputStream(file);
            int len = 0;
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            while ((len = is.read(buffer,0,bufferSize)) >= 0) {
                zos.write(buffer,0,len);
            }
            zos.closeEntry();
            logger.info("___compressFile close!");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("___Err." + e);
        } finally {
            try{
                if(is != null) {
                    is.close();
                }
            }catch(IOException ex){
                ex.printStackTrace();
                logger.error("___Err." + ex);
            }
        }

    }
}
