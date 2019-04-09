package com.xlauncher.service;

import com.xlauncher.entity.Predict;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletInputStream;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/18 0018
 * @Desc :
 **/
@Service
public interface PredictService {

    /**
     * 单条预测数据
     * @param id id
     * @return Predict
     */
    Predict getPredict(int id);

    /**
     * 历史预测列表查询
     * @param upStartTime 查询开始时间
     * @param lowStartTime 查询结束时间
     * @param sid 编号
     * @param identifyDescription 状态描述
     * @param number 页码数
     * @return List
     */
    List<Predict> listPredict(@Param("upStartTime") String upStartTime, @Param("lowStartTime") String lowStartTime
            , @Param("sid") String sid, @Param("identifyDescription") String identifyDescription, @Param("number") int number);

    /**
     * 历史预测列表查询count数用于分页
     * @param upStartTime 查询开始时间
     * @param lowStartTime 查询结束时间
     * @param sid 编号
     * @param identifyDescription 状态描述
     * @return int
     */
    int countPredict(@Param("upStartTime") String upStartTime, @Param("lowStartTime") String lowStartTime
            , @Param("sid") String sid, @Param("identifyDescription") String identifyDescription);

    /**
     * 存储预测数据
     * @param map map
     * @return int
     */
    int insertPredict(Map<String, Object> map);

    /**
     * 获取数据库最新时间作为查询的开始时间
     *
     * @return
     */
    String getCollectTime1();

    /**
     * 获取图片
     * @param id id
     * @return int
     */
    byte[] getImgData(int id);

    /**
     * 获取缩略图
     * @param id id
     * @return int
     */
    byte[] getPicData(int id);

    /**
     * 更新修改预测数据
     * @param predict 实例对象
     * @return int
     */
    int updatePredict(Predict predict);

    /**
     * 上传图片返回识别数据
     * @param bytes 图片数组
     * @param maxHeight 水尺总长度
     * @return Map
     */
    Map<String, Object> getImage(MultipartFile bytes, int maxHeight);

    /**
     * 统计历史所有/最近正常、异常数据占比
     * @return Map
     */
    Map<String, Object> countErrPredict();

    /**
     * 预测正确性统计
     * @return Map
     */
    Map<String, Object> countCheckPredict();
}
