package com.xlauncher.web;

import com.alibaba.fastjson.JSONObject;
import com.xlauncher.dao.UserDao;
import com.xlauncher.entity.Length;
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

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @Auther :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/11/1 0001
 * @Desc :
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml","classpath:spring-*.xml"})
@Transactional
public class LengthControllerTest {
    @Autowired
    WebApplicationContext context;
    @Autowired
    UserDao userDao;
    private static String token = "token is for test";
    private MockMvc mockMvc;
    private static Length length;
    @Before
    public void before() {
        //可以对所有的controller来进行测试
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        User user = new User();
        user.setAccount("LengthControllerTest");
        user.setPassword("111111");
        user.setToken(token);
        userDao.initUser(user);

    }
    @BeforeClass()
    public static void init() {
        length = new Length();
        length.setSid("4135821452");
        length.setChannel(1);
        length.setHeight(110);
    }
    @Test
    public void insertLength() throws Exception {
        String requestInfo = JSONObject.toJSONString(length);
        String responseString = mockMvc.perform(
                post("/length")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token",token)
                        .content(requestInfo))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------返回的json--------" + responseString);
    }

    @Test
    public void updateLength() throws Exception {
        Length lengthUp = new Length();
        lengthUp.setId(11);
        lengthUp.setSid("4135821452");
        lengthUp.setChannel(1);
        lengthUp.setHeight(110);
        String requestInfo = JSONObject.toJSONString(lengthUp);
        String responseString = mockMvc.perform(
                put("/length")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token",token)
                        .content(requestInfo))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------返回的json--------" + responseString);
    }

    @Test
    public void getLengthList() throws Exception {
        String responseString = mockMvc.perform(
                get("/length/{page}",1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token",token)
                        .param("sid","undefined")
                        .param("channel","1"))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------返回的json--------" + responseString);
    }

    @Test
    public void getCount() throws Exception {
        String responseString = mockMvc.perform(
                get("/length/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token",token)
                        .param("sid","undefined")
                        .param("channel","1"))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------返回的json--------" + responseString);
    }

    @Test
    public void download() throws Exception {
        String responseString = mockMvc.perform(
                get("/length/download")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header("token",token))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------返回的json--------" + responseString);
    }

    @Test
    public void exportExcel() throws Exception {
        String responseString = mockMvc.perform(
                get("/length/export")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header("token",token)
                        .param("channel","1"))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------返回的json--------" + responseString);
    }

    @Test
    public void importExcel() throws Exception {
        System.out.println("--------importExcel--------");
    }

}