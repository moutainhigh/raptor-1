package com.mo9.raptor.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by jyou on 2018/10/25.
 *
 * @author jyou
 */
@Component
public class MyApplicationContextUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext contex) throws BeansException {
        MyApplicationContextUtil.context = contex;
    }

    public static ApplicationContext getContext(){
        return context;
    }
}
