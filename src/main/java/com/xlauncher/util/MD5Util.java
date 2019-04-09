package com.xlauncher.util;

import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5(Message Digest algorithm 5，信息摘要算法)
   通常我们不直接使用上述MD5加密。通常将MD5产生的字节数组交给BASE64再加密一把，得到相应的字符串
   Digest:汇编
 * @author 白帅雷
 * @date 2018-08-16
 */
public class MD5Util {
    private static final String KEY_MD5 = "MD5";
    public static Logger logger = Logger.getLogger(MD5Util.class);
    public static String getResult(String inputStr) {
        logger.info("======== 加 密 前=======" + inputStr);
        BigInteger bigInteger = null;
        try {
            MessageDigest md = MessageDigest.getInstance(KEY_MD5);
            byte[] inputData = inputStr.getBytes();
            md.update(inputData);
            bigInteger = new BigInteger(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (bigInteger != null) {
            logger.info("======== 加 密 后 ========" + bigInteger.toString(16));
        }
        // 返回次bigInteger的给定基数16进制的字符串表示。
        return bigInteger.toString(16);
    }

}
