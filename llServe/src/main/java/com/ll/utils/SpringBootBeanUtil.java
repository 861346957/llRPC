package com.ll.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author liang.liu
 * @date createTime：2021/5/1 10:35
 */
@Component
public class SpringBootBeanUtil implements ApplicationContextAware {
    public static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringBootBeanUtil.applicationContext == null) {
            SpringBootBeanUtil.applicationContext=applicationContext;
        }
    }
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    public static Object getBean(String name){
       return applicationContext.getBean(name);
    }
    public static<T> T getBean(Class<T> clazz){
        return applicationContext.getBean(clazz);
    }
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }
}
