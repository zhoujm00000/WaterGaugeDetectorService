package com.xlauncher.web;

import com.alibaba.fastjson.JSONObject;
import com.xlauncher.dao.UserDao;
import com.xlauncher.entity.Configuration;
import com.xlauncher.entity.User;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
public class ConfiControllerTest {
    @Autowired
    WebApplicationContext context;
    @Autowired
    UserDao userDao;
    private static String token = "token is for test";
    private MockMvc mockMvc;
    private static Configuration configuration;
    @Before
    public void before() {
        //可以对所有的controller来进行测试
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        User user = new User();
        user.setAccount("ConfiControllerTest");
        user.setPassword("111111");
        user.setToken(token);
        userDao.initUser(user);

    }
    @BeforeClass()
    public static void init() {
        configuration = new Configuration();
        configuration.setOptions("ConfiControllerTest");
        Map<String, Object> map = new HashMap<>(1);
        map.put("data","data");
        configuration.setParams(map);
    }
    @Test
    public void insertConfi() throws Exception {
        String requestInfo = JSONObject.toJSONString(configuration);
        String responseString = mockMvc.perform(
                post("/confi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token",token)
                        .content(requestInfo))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------返回的json--------" + responseString);
    }

    @Test
    public void updateConfi() throws Exception {
        Configuration upConfi = new Configuration();
        upConfi.setId(1);
        upConfi.setOptions("MySQL");
        Map<String, Object> map = new HashMap<>(1);
        map.put("port","3306");
        upConfi.setParams(map);
        String requestInfo = JSONObject.toJSONString(upConfi);
        String responseString = mockMvc.perform(
                put("/confi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token",token)
                        .content(requestInfo))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------返回的json--------" + responseString);
    }

    @Test
    public void getConfi() throws Exception {
        String responseString = mockMvc.perform(
                get("/confi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token",token))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------返回的json--------" + responseString);
    }

}