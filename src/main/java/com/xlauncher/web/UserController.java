package com.xlauncher.web;

import com.xlauncher.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/17 0017
 * @Desc :
 **/
@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;
    private static Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户登录验证信息正确则返回token等
     * @param userLogin 用户登录信息
     * @param response response
     * @return Map
     */
    @PostMapping("/login")
    public Map<String,Object> login(@RequestBody Map<String,Object> userLogin, HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> responseMap = new HashMap<>(1);
        responseMap.put("account",userLogin.get("account"));
        logger.info("user:" + userLogin);
        return userService.login(userLogin,request,response,responseMap);
    }

    /**
     * 用户退出登录session失效
     * @param request request
     * @return Map
     */
    @PostMapping(value = "/logout")
    public Map<String, String> logout(@RequestBody Map<String,String> userForLogout, HttpServletRequest request) {
        Map<String, String> map = new HashMap<>(1);
        logger.info("user:" + userForLogout);
        this.userService.logout(request.getHeader("token"));
        map.put("code", "200");
        return map;
    }

    /**
     * 用户修改密码
     *
     * @param request request
     * @return Map
     */
    @PutMapping()
    public int update(@RequestBody Map<String,Object> user, HttpServletRequest request) {
        return this.userService.updateToken(user);
    }
}
