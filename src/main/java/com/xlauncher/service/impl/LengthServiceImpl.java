package com.xlauncher.service.impl;

import com.xlauncher.dao.LengthDao;
import com.xlauncher.entity.Length;
import com.xlauncher.service.LengthService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/25 0025
 * @Desc :水尺总长配置实现类
 **/
@Service
public class LengthServiceImpl implements LengthService{

    private final LengthDao lengthDao;
    private static Logger logger = Logger.getLogger(LengthServiceImpl.class);
    @Autowired
    private LengthServiceImpl(LengthDao lengthDao) {
        this.lengthDao = lengthDao;
    }
    /**
     * Excel导入或手工录入(存在则更新，不存在就保存)
     *
     * @param length 水尺实体对象
     * @return 影响数据库的行数
     */
    @Override
    public int insertLength(Length length) {
        logger.info("保存数据length:" + length);
        String sid = length.getSid();
        int channel = length.getChannel();
        int count = check(sid, channel);
        if (count > 1) {
            return 0;
        } else if (count == 1) {
            int id = lengthDao.getLength(sid, channel).getId();
            length.setId(id);
            return lengthDao.updateLength(length);
        }
        return lengthDao.insertLength(length);
    }

    /**
     * 查重
     *
     * @param sid 站点编号
     * @param channel 摄像头编号
     * @return 影响数据库行数
     */
    private int check(String sid, int channel) {
        return lengthDao.getCount(sid, channel);
    }

    /**
     * 可修改水尺长度
     *
     * @param length 水尺实体对象
     * @return 影响数据库行数
     */
    @Override
    public int updateLength(Length length) {
        String sid = length.getSid();
        int channel = length.getChannel();
        int count = check(sid, channel);
        if (count > 1) {
            return 0;
        }
        return lengthDao.updateLength(length);
    }

    /**
     * 删除配置
     *
     * @param id 数据库编号
     * @return 影响数据库行数
     */
    @Override
    public int deleteLength(int id) {
        return this.lengthDao.deleteLength(id);
    }

    /**
     * 得到水尺总长用于导出Excel
     *
     * @param sid 站点编号
     * @param channel 摄像头编号
     * @return 影响数据库行数
     */
    @Override
    public List<Length> getLengthListForExcel(String sid, int channel) {
        return lengthDao.getLengthListForExcel(sid,channel);
    }


    /**
     * 得到水尺总长
     *
     * @param sid 站点编号
     * @param channel 摄像头编号
     * @return 影响数据库行数
     */
    @Override
    public List<Length> getLengthList(String sid, int channel, int num) {
        return lengthDao.getLengthList(sid,channel, num);
    }

    /**
     * 得到水尺总长
     *
     * @param sid 站点编号
     * @param channel 摄像头编号
     * @return 影响数据库行数
     */
    @Override
    public int getLength(String sid, int channel) {
        Length length = lengthDao.getLength(sid, channel);
        if (length != null) {
            return length.getHeight();
        } else {
            return 0;
        }
    }

    /**
     * count
     *
     * @param sid 站点编号
     * @param channel 摄像头编号
     * @return 影响数据库行数
     */
    @Override
    public int count(String sid, int channel) {
        return lengthDao.count(sid,channel);
    }
}
