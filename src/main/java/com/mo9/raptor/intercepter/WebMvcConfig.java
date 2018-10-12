package com.mo9.raptor.intercepter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 * Created by sxu on 2018/2/28.
 * @author jyou
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private AddLoanOrderInterceptor addLoanOrderInterceptor;

    @Value("${sign.switch}")
    private String signSwitch;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Boolean aBoolean = Boolean.valueOf(signSwitch);
        if(aBoolean){
            registry.addInterceptor(authInterceptor).addPathPatterns("/**");
            registry.addInterceptor(addLoanOrderInterceptor).addPathPatterns("/order/add");
        }
    }




}
