package com.xlauncher.web;

import com.xlauncher.service.ConsumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/19 0019
 * @Desc :
 **/
@RestController
@RequestMapping(value = "/consume")
public class ConsumeController {
    private final
    ConsumeService consumeService;

    @Autowired
    public ConsumeController(ConsumeService consumeService) {
        this.consumeService = consumeService;
    }

    /**
     * 每30天平均一轮数据读取消耗时间
     * @return Map
     */
    @GetMapping("/avg")
    public Map<String, Object> avgConsumeTimeByDay(@RequestHeader("token") String token){
        return consumeService.avgConsumeTimeByDay();
    }
}
