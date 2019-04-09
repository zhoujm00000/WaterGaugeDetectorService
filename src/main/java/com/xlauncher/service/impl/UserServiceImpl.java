package com.xlauncher.service.impl;

import com.xlauncher.dao.UserDao;
import com.xlauncher.entity.User;
import com.xlauncher.service.UserService;
import com.xlauncher.util.MD5Util;
import com.xlauncher.util.userlogin.Jwt;
import com.xlauncher.util.userlogin.SessionManageListener;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/17 0017
 * @Desc :
 **/
@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private static Logger logger = Logger.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 用户登录
     * @param userLogin   用户登录信息
     * @param request request
     * @param response response
     * @param responseMap responseMap
     * @return Map
     */
    @Override
    public Map<String, Object> login(Map<String, Object> userLogin, HttpServletRequest request
            , HttpServletResponse response, Map<String, Object> responseMap) {
        logger.info("用户登录user:" + userLogin);
        User user = userDao.getUserByAccount( (String) userLogin.get("account"));
        if(user != null && MD5Util.getResult((String) userLogin.get("password")).equals(user.getPassword())) {
            String token = Jwt.sign(responseMap, 12 * 60 * 60 * 1000L);
            if (token != null) {
                user.setToken(token);
            }
//            User userIsLogin = new User();
//            userIsLogin.setAccount((String) userLogin.get("account"));
//            Boolean hasLogin = SessionManageListener.checkIfHasLogin(user.getAccount());
//            if (hasLogin) {
//                responseMap.put("202", "The current user is logged in! Continue to log in?");
//                response.setStatus(202);
//                String confirm = request.getHeader("continue");
//                String eventTrue = "true";
//                if (confirm != null && confirm.equals(eventTrue)) {
//                    if (SessionManageListener.containsKey(request.getSession().getId())) {
//                        responseMap.put("406", "A browser cannot log in multiple users at once!");
//                        response.setStatus(406);
//                        return responseMap;
//                    }
//                    SessionManageListener.removeUserSession(user.getAccount());
//                    SessionManageListener.removeSession(request.getSession().getId());
//                    request.getSession().setAttribute("user", userIsLogin);
//                    SessionManageListener.addUserSession(request.getSession());
//                    responseMap.put("token", user.getToken());
//                    responseMap.put("code","200");
//                    userDao.saveToken(token);
//                }
//            } else {
//                if (SessionManageListener.containsKey(request.getSession().getId())) {
//                    responseMap.put("406", "A browser cannot log in multiple users at once!");
//                    response.setStatus(406);
//                    return responseMap;
//                }
//                request.getSession().setAttribute("user", userIsLogin);
                SessionManageListener.addUserSession(request.getSession());
                userDao.saveToken(token);
                responseMap.put("token", user.getToken());
                responseMap.put("code","200");
//            }
        } else {
            responseMap.put("Err","Input is not correct! Please log in again!!!!!");
            responseMap.put("code","404");
        }
        return responseMap;
    }

    /**
     * @param token 令牌
     */
    @Override
    public void logout(String token) {
        logger.info("用户退出");
        userDao.deleteToken(token);
    }

    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    @Override
    public int updateToken(Map<String,Object> user) {
        User user1 = new User();
        user1.setAccount(String.valueOf(user.get("account")));
        user1.setPassword(MD5Util.getResult(String.valueOf(user.get("password"))));
        user1.setToken(String.valueOf(user.get("token")));
        return userDao.updateToken(user1);
    }
}
