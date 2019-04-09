package com.xlauncher.service;

import com.xlauncher.entity.Length;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/25 0025
 * @Desc :
 **/
@Service
public interface LengthService {
    /**
     * Excel导入或手工录入
     * @param length
     * @return
     */
    int insertLength(Length length);

    /**
     * 可修改水尺长度
     * @param length
     * @return
     */
    int updateLength(Length length);

    /**
     * 删除配置
     * @param id
     * @return
     */
    int deleteLength(int id);

    /**
     * 得到水尺总长用于导出Excel
     *
     * @param sid
     * @param channel
     * @return
     */
    List<Length> getLengthListForExcel(@Param("sid") String sid, @Param("channel") int channel);

    /**
     * 得到水尺总长可展示
     *
     * @param sid
     * @param num
     * @param channel
     * @return
     */
    List<Length> getLengthList(@Param("sid") String sid, @Param("channel") int channel, @Param("num") int num);

    /**
     * 得到水尺总长
     *
     * @param sid
     * @param channel
     * @return
     */
    int getLength(@Param("sid") String sid, @Param("channel") int channel);

    /**
     * count
     *
     * @param sid
     * @param channel
     * @return
     */
    int count(@Param("sid") String sid, @Param("channel") int channel);

}
