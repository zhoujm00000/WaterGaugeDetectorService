package com.xlauncher.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 自定义调用bean
 * @author baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/9/14 0014
 * @Desc :
 **/
@Component
public class SpringContextUtil implements ApplicationContextAware{
    /**
     * spring应用上下文
     */
    private static ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的回调方法，设置上下文环境
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;

    }
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    /**
     * 获取对象 这里重写了bean方法，起主要作用
     */
    public static Object getBean(String beanId) throws BeansException {
        return applicationContext.getBean(beanId);
    }
}
