package com.xlauncher.web;

import com.alibaba.fastjson.JSONObject;
import com.xlauncher.dao.UserDao;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @Auther :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/17 0017
 * @Desc :
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml","classpath:spring-*.xml"})
@Transactional
public class UserControllerTest {
    @Autowired
    WebApplicationContext context;
    @Autowired
    UserDao userDao;
    private static String token = "token is for test";
    private MockMvc mockMvc;
    @Before
    public void before() {
        //可以对所有的controller来进行测试
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        User user = new User();
        user.setAccount("UserControllerTest");
        user.setPassword("111111");
        user.setToken(token);
        userDao.initUser(user);
    }
    @BeforeClass()
    public static void init() {

    }
    @Test
    public void login() throws Exception {
        Map<String,Object> userLogin = new HashMap<>(1);
        userLogin.put("account","admin");
        userLogin.put("password","111111");
        String requestInfo = JSONObject.toJSONString(userLogin);
        String responseString = mockMvc.perform(
                post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestInfo))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------login--------" + responseString);
    }

    @Test
    public void logout() throws Exception {
        Map<String,Object> userLogin = new HashMap<>(1);
        userLogin.put("account","admin");
        userLogin.put("password","111111");
        String requestInfo = JSONObject.toJSONString(userLogin);
        String responseString = mockMvc.perform(
                post("/user/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestInfo))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------logout--------" + responseString);
    }

}