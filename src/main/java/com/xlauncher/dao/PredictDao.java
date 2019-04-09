package com.xlauncher.dao;

import com.xlauncher.entity.Predict;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/18 0018
 * @Desc :预测信息DAO层
 **/
@Service
public interface PredictDao {


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
     * @param predict 实例对象
     * @return int
     */
    int insertPredict(Predict predict);

    /**
     * 获取图片
     * @param id id
     * @return int
     */
    Predict getImgData(int id);

    /**
     * 缩略图
     * @param id id
     * @return int
     */
    Predict getPicData(int id);

    /**
     * 获取数据库最新时间作为查询的开始时间
     * @return
     */
    String getCollectTime1();

    /**
     * 更新修改预测数据
     * @param predict 实例对象
     * @return int
     */
    int updatePredict(Predict predict);

    /**
     * 所有历史预测数据总数
     * @param status 数据是否正常的状态
     * @return int
     */
    int countPredictByStatus(int status);

    /**
     * 最近num+1天历史预测数据总数
     * @param status 数据是否正常的状态
     * @param num 天数
     * @return int
     */
    int countPredictByStatusNum(@Param("status") int status, @Param("num") int num);

    /**
     * 预测正确性统计
     * @return int
     */
    int countPredictByCheck();

    /**
     * 预测正确性统计
     * @return int
     */
    int countPredictTrue();

    /**
     * 查重
     * @param collectTime 时间
     * @param sid 站点编号
     * @return 1为已存在，0为不存在
     */
    int predictExistence(@Param("collectTime")String collectTime, @Param("sid")String sid);

    /**
     * 得到半年以上de异常数据不包括
     *
     * @return List
     */
    List<Predict> getHalfYearPredict();
    /**
     * 得到半年以上de异常数据
     *
     * @return List
     */
    List<Predict> getHalfAllYearPredict();

    /**
     * 根据时间获取预测数据并返回给第三方数据库
     *
     * @param startTime 预测开始时间
     * @return List
     */
    List<Predict> getPredictByTime(String startTime);

    /**
     * 清空source数据
     *
     * @param id id
     * @return int
     */
    int emptySource(int id);
}
