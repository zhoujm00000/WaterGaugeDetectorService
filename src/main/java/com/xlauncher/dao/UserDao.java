package com.xlauncher.dao;

import com.xlauncher.entity.User;
import org.springframework.stereotype.Service;

/**
 * @author baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/17 0017
 * @Desc :DAO层
 **/
@Service
public interface UserDao {

    /**
     * 根据用户名来查询用户
     * @param account 用户名
     * @return 模糊查询结果
     */
    User getUserByAccount(String account);

    /**
     * 保存token
     * @param token 用户令牌
     * @return int
     */
    int saveToken(String token);

    /**
     * 注销token
     * @param token 用户令牌
     */
    void deleteToken(String token);

    /**
     * 修改用户
     * @param user
     * @return
     */
    int updateToken(User user);

    /**
     * 验证token
     * @param token 用户令牌
     * @return
     */
    User checkToken(String token);

    /**
     * 初始化用户
     * @param user user
     */
    void initUser(User user);
}
