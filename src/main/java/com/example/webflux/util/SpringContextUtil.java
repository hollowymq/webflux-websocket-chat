package com.example.webflux.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @program: vue-cli-rest
 * @description: spring上下文工具类
 * @author: Yang Mingqiang
 * @create: 2020-07-06 09:56
 * @vsersion: V1.0
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * Description:
     * 〈获取applicationContext〉
     *
    []
     * @return : org.springframework.context.ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Description:
     * 〈通过name获取 Bean.〉
     *
    [name]
     * @return : java.lang.Object
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * Description:
     * 〈通过class获取Bean.〉
     *
    [clazz]
     * @return : T
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * Description:
     * 〈通过name,以及Clazz返回指定的Bean〉
     *
    [name, clazz]
     * @return : T
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}