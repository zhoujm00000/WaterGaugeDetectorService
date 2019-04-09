package com.xlauncher.web;

import com.xlauncher.entity.Configuration;
import com.xlauncher.service.ConfiService;
import com.xlauncher.util.JDBCUtil;
import com.xlauncher.util.SaveErrImgUtil;
import com.xlauncher.util.ThreadUtil;
import com.xlauncher.util.userlogin.ActiveUtil;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/17 0017
 * @Desc :
 **/
@RequestMapping(value = "/confi")
@RestController
public class ConfiController {

    @Autowired
    private ConfiService confiService;
    @Autowired
    ActiveUtil activeUtil;
    @Autowired
    JDBCUtil jdbcUtil;
    @Autowired
    ThreadUtil threadUtil;
    @Autowired
    SaveErrImgUtil saveErrImgUtil;
    private static Logger logger = Logger.getLogger(ConfiController.class);

    /**
     * 保存配置信息
     * @param configuration 配置信息
     * @return int
     */
    @PostMapping("")
    public int insertConfi(@RequestBody @Param("configuration") Configuration configuration
            , @RequestHeader("token") String token){
        logger.info("保存配置信息insertConfi：" + configuration);
        return confiService.insertConfi(configuration);
    }

    /**
     * 可修改配置信息
     *
     * @param list 配置信息
     * @return int
     */
    @PutMapping("")
    public Map<String, Object> updateConfi(@RequestBody @Param("list") List<Configuration> list
            , @RequestHeader("token") String token, HttpServletRequest request, HttpServletResponse response){
//        activeUtil.check(request, response);
        logger.info("更新配置信息updateConfi：" + list);
        Map<String, Object> map = new HashMap<>(1);
        Map<String, Object> confirm = list.get(0).getParams();
        logger.info("____confirm." + confirm);
        int con = 0;
        try {
             con = jdbcUtil.confirm(confirm);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" Err.___jdbcUtil.confirm failure:" + e);
            map.put("code", 405);
            map.put("msg","配置验证超时!" + e);
        }
        if (con == 1) {
            logger.info(" ___jdbcUtil.confirm success." + con);
            int status = confiService.updateConfi(list);
            if (status == 1) {
                logger.info(" ___update success." + status);
                threadUtil.run();
                map.put("status", status);
                map.put("code", 200);
                map.put("msg","保存成功!");
            } else {
                map.put("status", status);
                map.put("code", 200);
                map.put("msg","保存失败!");
            }
        } else {
            logger.info(" ___jdbcUtil.confirm failure." + con);
            map.put("code", 405);
            map.put("msg","配置错误!");
        }
        return map;
    }

    /**
     * 获取配置信息便于展示
     * @return List
     */
    @GetMapping("")
    public List<Configuration> getConfi(@RequestHeader("token") String token, HttpServletRequest request, HttpServletResponse response){
//        activeUtil.check(request, response);
        return confiService.listConfi();
    }


}
