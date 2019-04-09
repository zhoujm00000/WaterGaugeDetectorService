package com.xlauncher.web;

import com.alibaba.fastjson.JSONObject;
import com.xlauncher.dao.UserDao;
import com.xlauncher.entity.Predict;
import com.xlauncher.entity.User;
import com.xlauncher.util.DatetimeUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @Auther :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/19 0019
 * @Desc :
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml","classpath:spring-*.xml"})
@Transactional
@WebAppConfiguration
public class PredictControllerTest {
    @Autowired
    WebApplicationContext context;
    @Autowired
    UserDao userDao;
    private static String token = "token is for test";
    private MockMvc mockMvc;
    private static Predict predict;
    @Before
    public void before() {
        //可以对所有的controller来进行测试
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        User user = new User();
        user.setAccount("PredictControllerTest");
        user.setPassword("111111");
        user.setToken(token);
        userDao.initUser(user);

    }
    @BeforeClass()
    public static void init() {
        predict = new Predict();
        predict.setCollectTime(DatetimeUtil.getDate(System.currentTimeMillis()));
        predict.setChannel(1001);
        predict.setSid("1001");
    }
    @Test
    public void listPredict() throws Exception {
        String responseString = mockMvc.perform(
                get("/predict/list/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token",token)
                        .param("upStartTime","undefined")
                        .param("lowStartTime","undefined")
                        .param("sid","undefined"))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------listPredict--------" + responseString);
    }

    @Test
    public void countPredict() throws Exception {
        String responseString = mockMvc.perform(
                get("/predict/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token",token)
                        .param("upStartTime","undefined")
                        .param("lowStartTime","undefined")
                        .param("sid","undefined"))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------countPredict--------" + responseString);
    }

    @Test
    public void insertPredict() throws Exception {
        String requestInfo = JSONObject.toJSONString(predict);
        String responseString = mockMvc.perform(
                put("/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token",token)
                        .content(requestInfo))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------insertPredict--------" + responseString);
    }

    @Test
    public void updatePredict() throws Exception {
        Predict upPre = new Predict();
        upPre.setId(1);
        upPre.setCheck((float) 50);
        upPre.setStatus(0);
        String requestInfo = JSONObject.toJSONString(upPre);
        String responseString = mockMvc.perform(
                put("/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token",token)
                        .content(requestInfo))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------updatePredict--------" + responseString);
    }

    @Test
    public void getErrPredictCount() throws Exception {
        String responseString = mockMvc.perform(
                get("/predict/getErr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token",token))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------getErrPredictCount--------" + responseString);
    }

    @Test
    public void getAccuracyPredict() throws Exception {
        String responseString = mockMvc.perform(
                get("/predict/accuracy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token",token))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------getAccuracyPredict--------" + responseString);
    }

}