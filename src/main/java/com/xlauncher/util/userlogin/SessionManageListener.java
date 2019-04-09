package com.xlauncher.util.userlogin;

import com.xlauncher.entity.User;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * session监听管理类
 * @date 2018-03-06
 * @author 白帅雷
 */
public class SessionManageListener implements HttpSessionListener {

    /**
     * logger
     */
    private Logger logger = Logger.getLogger(this.getClass());

    /**
     * 映射关系：key为sessionId，value为HttpSession，
     */
    private static Map<String,HttpSession> sessionMap = new ConcurrentHashMap<>(1000);


    /**
     * HttpSessionListener中的方法
     * 创建session
     * @param event
     */
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        session.setMaxInactiveInterval(60 * 60);
        logger.info("web容器session对象被创建sessionCreated, sid: " + session.getId());
    }

    /**
     * HttpSessionListener中的方法
     * 销毁session时，删除sessionMap中对应的session
     * @param event
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        session.invalidate();
        String sessionId = session.getId();
        getSessionMap().remove(sessionId);
        for (String key : getUserSessionMap().keySet()){
            if (getUserSessionMap().get(key).equals(sessionId)){
                getSessionMap().remove(sessionId);
                getUserSessionMap().remove(key);
                getUserSessions().remove(session);
            }
        }
        logger.debug("web容器有session对象被销毁sessionDestroyed: " + session.getId());
    }


    /**
     * 得到在线用户会话集合
     * 即session
     * @return
     */
    public static List<HttpSession> getUserSessions(){
        List<HttpSession> userSessionList  = new ArrayList<HttpSession>();
        Iterator<String> iterator = getSessionMapKeySetIt();
        while (iterator.hasNext()){
            String key = iterator.next();
            HttpSession session = getSessionMap().get(key);
            userSessionList.add(session);
        }
        return userSessionList;
    }

    /**
     * 得到用户对应会话map，key为userLoginName，value为sessionId
     * 即(userLoginName:sessionId)
     * @return
     */
    public static Map<String, String> getUserSessionMap(){
        Map<String, String> usersSessionMap = new HashMap<String,String>();
        Iterator<String> iterator = getSessionMapKeySetIt();
        while (iterator.hasNext()){
            String sessionId = iterator.next();
            HttpSession session = getSessionMap().get(sessionId);
            User User = (User) session.getAttribute("User");
            if (User != null){
                usersSessionMap.put(User.getAccount(),sessionId);
            }
        }
        return usersSessionMap;
    }

    /**
     * 移除用户session
     * 即 userSessionMap
     * @param userLoginName
     */
    public synchronized static void removeUserSession(String userLoginName){
        Map<String,String> userSessionMap = getUserSessionMap();
        if (userSessionMap.containsKey(userLoginName)){
            String sessionId = userSessionMap.get(userLoginName);
            getSessionMap().get(sessionId).invalidate();
            getSessionMap().remove(sessionId);
            getUserSessions().remove(getSessionMap().get(sessionId));
        }
    }

    /**
     * 增加用户到session集合中
     * 即 userSessions,userSessionMap,sessionMap
     * @param session
     */
    public static void addUserSession(HttpSession session){
        getSessionMap().put(session.getId(),session);
        getUserSessionMap().put(String.valueOf(session.getAttribute("User")),session.getId());
        getUserSessions().add(session);
    }

    /**
     * 移除一个session
     * 即 sessionMap
     * @param sessionId
     */
    public static void removeSession(String sessionId){
        getSessionMap().remove(sessionId);
    }

    /**
     * sessionMap
     * @param key
     * @return
     */
    public static boolean containsKey(String key){
        return getSessionMap().containsKey(key);
    }

    /**
     * 判断该用户是否已重复登录，使用同步方法，只允许一个线程进入，才好验证是否重复登录
     * @param loginName
     * @return
     */
    public synchronized static boolean checkIfHasLogin(String loginName){
        Iterator<String> iterator = getSessionMapKeySetIt();
        boolean hasLogin = false;
        while (iterator.hasNext()){
            String sessionId = iterator.next();
            HttpSession session = getSessionMap().get(sessionId);
            User userIsCheck = (User) session.getAttribute("user");
            if (userIsCheck.getAccount().equals(loginName)){
                hasLogin = true;
                break;
            }
        }
        return hasLogin;
    }

    /**
     * 获取在线的sessionMap
     * @return
     */
    public static Map<String,HttpSession> getSessionMap(){
        return sessionMap;
    }

    /**
     * 获取在线sessionMap的sessionId
     * 一个map对象，使用KetSet()方法获取所有的key值
     * @return
     */
    public static Iterator<String> getSessionMapKeySetIt(){
        return getSessionMap().keySet().iterator();
    }

}
