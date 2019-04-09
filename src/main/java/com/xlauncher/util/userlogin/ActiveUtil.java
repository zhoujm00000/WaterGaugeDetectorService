package com.xlauncher.util.userlogin;

import com.xlauncher.dao.UserDao;
import com.xlauncher.entity.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 用户活跃状态判断
 * a.用户处于活跃状态则执行操作
 * b.用户处于不活跃状态则警告
 * @date 2018-03-02
 * @author 白帅雷`
 */
@Component
public class ActiveUtil {

    private final
    UserDao userDao;
    private static Logger logger = Logger.getLogger(ActiveUtil.class);

    @Autowired
    public ActiveUtil(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 对token解码判断是否过期
     */
    public void check(HttpServletRequest request,HttpServletResponse response) {

        User unSign = Jwt.unSign(request.getHeader("token"), User.class);
        User userCheck = userDao.checkToken(request.getHeader("token"));
        if (userCheck != null) {
            if (unSign == null) {
                /*
                  token已过期
                 */
                logger.info("ActiveUtil-token已过期!");
                userDao.deleteToken(request.getHeader("token"));
                SessionManageListener.removeUserSession(userCheck.getAccount());
                SessionManageListener.removeSession(request.getSession().getId());
                response.setStatus(415);
            } else {
                /*
                  token未过期
                 */
                if (request.getSession(false) == null) {
                    logger.info("ActiveUtil-token未过期, session失效!");
                    userDao.deleteToken(request.getHeader("token"));
                    SessionManageListener.removeUserSession(userCheck.getAccount());
                    SessionManageListener.removeSession(request.getSession().getId());
                    response.setStatus(415);
                } else {
                    if (!SessionManageListener.containsKey(request.getSession(false).getId())) {
                        logger.info("ActiveUtil-token未过期, sessionId不存在!");
                        userDao.deleteToken(request.getHeader("token"));
                        SessionManageListener.removeUserSession(userCheck.getAccount());
                        SessionManageListener.removeSession(request.getSession().getId());
                        response.setStatus(415);
                    }
                }
            }
        } else {
            logger.info("ActiveUtil-token已过期不存在!");
            SessionManageListener.removeSession(request.getSession().getId());
            response.setStatus(415);
        }
    }

}
