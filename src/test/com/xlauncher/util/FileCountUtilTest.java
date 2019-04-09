package com.xlauncher.util;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * @Auther :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/11/1 0001
 * @Desc :
 **/
public class FileCountUtilTest {
    @Test
    public void getFileCount() throws Exception {
        FileCountUtil.getFileCount("");
    }

    @Test
    public void deleteAllFile() throws Exception {
        FileCountUtil.deleteAllFile("D:\\imgs");
    }

}