package com.xlauncher.web;

import com.xlauncher.entity.Predict;
import com.xlauncher.service.PredictService;
import com.xlauncher.util.userlogin.ActiveUtil;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/18 0018
 * @Desc :
 **/
@RestController
@RequestMapping(value = "/predict")
public class PredictController {
    @Autowired
    private PredictService predictService;
    @Autowired
    ActiveUtil activeUtil;
    private static Logger logger = Logger.getLogger(PredictController.class);

    /**
     * 历史预测列表查询
     * @param upStartTime 查询开始时间
     * @param lowStartTime 查询结束时间
     * @param sid 编号
     * @param identifyDescription 状态描述
     * @param number 页码数
     * @return Map
     */
    @GetMapping("/list/{number}")
    public Map<String, Object> listPredict(@RequestParam("upStartTime") String upStartTime, @RequestParam("lowStartTime") String lowStartTime
            , @RequestParam("sid") String sid, @RequestParam("identifyDescription") String identifyDescription, @RequestHeader("token") String token, @PathVariable("number") int number
            , HttpServletRequest request,HttpServletResponse response){
//        activeUtil.check(request, response);
        Map<String, Object> map = new HashMap<>(1);
        logger.info("历史预测列表查询listPredict:参数upStartTime:" + upStartTime + ", lowStartTime:" + lowStartTime + ", sid:" + sid + ", identifyDescription:" + identifyDescription);
        List<Predict> predictList = predictService.listPredict(upStartTime, lowStartTime, sid, identifyDescription, (number-1)*10);
        map.put("predictList", predictList);
        return map;
    }

    /**
     * 历史预测列表查询count数用于分页
     * @param upStartTime 查询开始时间
     * @param lowStartTime 查询结束时间
     * @param sid 编号
     * @param identifyDescription 状态描述
     * @return Map
     */
    @GetMapping("/count")
    public Map<String, Object> countPredict(@RequestParam("upStartTime") String upStartTime, @RequestParam("lowStartTime") String lowStartTime
            , @RequestParam("sid") String sid, @RequestParam("identifyDescription") String identifyDescription, @RequestHeader("token") String token
            , HttpServletRequest request,HttpServletResponse response){
//        activeUtil.check(request,response);
        Map<String, Object> map = new HashMap<>(1);
        int count = predictService.countPredict(upStartTime, lowStartTime, sid, identifyDescription);
        map.put("count", count);
        return map;
    }

    /**
     * 存储预测数据
     * @param predict 实例对象
     * @return Map
     */
    @PostMapping("")
    public Map<String, Object> insertPredict(@RequestBody Predict predict, @RequestHeader("token") String token
            , HttpServletRequest request,HttpServletResponse response){
//        activeUtil.check(request, response);
        logger.info("存储预测数据insertPredict:" + predict);
        Map<String, Object> map = new HashMap<>(1);
        int count = predictService.insertPredict(map);
        map.put("count", count);
        return map;
    }

    /**
     * 更新修改预测数据
     * @param predict 实例对象
     * @return Map
     */
    @PutMapping("")
    public Map<String, Object> updatePredict(@RequestBody @Param("predict") Predict predict, @RequestHeader("token") String token
            , HttpServletRequest request,HttpServletResponse response){
//        activeUtil.check(request, response);
        logger.info("更新预测数据updatePredict:" + predict);
        Map<String, Object> map = new HashMap<>(1);
        int count = predictService.updatePredict(predict);
        if (count == 1) {
            map.put("count", count);
            map.put("code", 200);
        } else {
            map.put("count", count);
            map.put("code", 400);
        }
        return map;
    }

    /**
     * 根据ID获取缩略图
     *
     * @param id ID
     * @param httpServletResponse 返回给前端的数据载体
     */
    @GetMapping(value = "/picture/{id}")
    public void getPicById(@PathVariable int id, HttpServletResponse httpServletResponse) {
        httpServletResponse.setContentType("image/jpg");
        httpServletResponse.setBufferSize(1024*12);
        try {
            OutputStream outputStream = httpServletResponse.getOutputStream();
            byte[] pic = this.predictService.getPicData(id);
            outputStream.write(pic);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            logger.error("获取缩略图失败！" + e.getMessage());
        }
    }


    /**
     * 根据ID获取图片
     *
     * @param id ID
     * @param httpServletResponse 返回给前端的数据载体
     * @return 获取图片状态的map和HttpServletResponse
     */
    @GetMapping(value = "/image/{id}")
    public Map<String, Object> getImgById(@PathVariable int id, HttpServletResponse httpServletResponse) {
        int ret;
        httpServletResponse.setContentType("image/jpg");
        httpServletResponse.setBufferSize(1024*24);
        try {
            OutputStream outputStream = httpServletResponse.getOutputStream();
            byte[] imgData = this.predictService.getImgData(id);
            outputStream.write(imgData);
            outputStream.flush();
            outputStream.close();
            ret = 1;
        } catch (IOException e) {
            logger.error("获取图片失败！" + e.getMessage());
            ret = 0;
        }
        Map<String, Object> map = new HashMap<>(1);
        map.put("status", ret);
        return map;
    }

    /**
     * 上传图片返回识别数据
     * @return Map
     */
    @PostMapping("/upload")
    public Map<String, Object> upLoadImage(@RequestBody MultipartFile multipartFile
            , @RequestHeader int maxHeight , HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> map = new HashMap<>(1);
//
//        int len = request.getContentLength();
//        ServletInputStream is = request.getInputStream();
//        byte[] bytes = new byte[len];
//
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        int index;
//        while ((index = is.read(bytes,0,len)) >0) {
//            bos.write(bytes,0, index);
//        }
//
//        predictService.getImage(bos.toByteArray(),maxHeight);

        if (multipartFile != null) {
            // 文件原名称
            String fileName=multipartFile.getOriginalFilename();
            logger.info("上传图片并返回识别数据fileName：" + fileName + ", maxHeight：" + maxHeight);
            // 文件类型
            String type= fileName.contains(".") ?fileName.substring(fileName.lastIndexOf(".")+1, fileName.length()):null;
            if (type != null) {
                map = predictService.getImage(multipartFile, maxHeight);
            } else {
                System.out.println("文件类型有误,重新上传!");
                response.setStatus(415);
                return null;
            }
        }
        return map;
    }

    /**
     * 统计历史所有/最近正常、异常数据占比
     * @return Map
     */
    @GetMapping("/getErr")
    public Map<String, Object> getErrPredictCount(@RequestHeader("token") String token){
        return predictService.countErrPredict();
    }

    /**
     * 预测正确性统计
     * @return Map
     */
    @GetMapping("/accuracy")
    public Map<String, Object> getAccuracyPredict(@RequestHeader("token") String token){
        return predictService.countCheckPredict();
    }
}
