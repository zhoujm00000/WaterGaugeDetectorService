package com.xlauncher.dao;

import com.xlauncher.entity.Length;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/25 0025
 * @Desc :水尺总长DAO层
 **/
@Service
public interface LengthDao {

    /**
     * Excel导入或手工录入
     *
     * @param length
     * @return
     */
    int insertLength(Length length);

    /**
     * 可修改水尺长度
     *
     * @param length
     * @return
     */
    int updateLength(Length length);

    /**
     * 得到水尺总长
     *
     * @param sid
     * @param channel
     * @return
     */
    Length getLength(@Param("sid") String sid, @Param("channel") int channel);

    /**
     * 删除配置
     * @param id
     * @return
     */
    int deleteLength(int id);

    /**
     * 清空数据
     * @return
     */
    int emptyLength();

    /**
     * 查重
     *
     * @param sid
     * @param channel
     * @return
     */
    int getCount(@Param("sid") String sid, @Param("channel") int channel);

    /**
     * 得到水尺总长用于导出Excel
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
     * count
     *
     * @param sid
     * @param channel
     * @return
     */
    int count(@Param("sid") String sid, @Param("channel") int channel);

}
