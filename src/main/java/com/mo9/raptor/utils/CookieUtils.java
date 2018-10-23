package com.mo9.raptor.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author zma
 * @date 2018/10/17
 */
public class CookieUtils {

    public  static String loginName = "LN_f4aq5wf";

    public static Integer EXPIRE_1D = 60 * 60 * 24;

    /**
     * 登录时设置cookie
     * @param response
     */
    public static String addLoginCookie(HttpServletResponse response){
        return addCookie(response,loginName, UUID.randomUUID().toString(),EXPIRE_1D);
    }


    /**
     * 添加到cookie
     * @param response
     * @param name
     * @param value
     * @param expirySecond
     */
    public static String addCookie(HttpServletResponse response,String name,String value,int expirySecond){
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(expirySecond);
        response.addCookie(cookie);
        return value;
    }

    /**
     * @param request
     * @return
     */
    public static String getLoginValue(HttpServletRequest request){
        return getValueFromCookies(request,loginName);
    }

    /**
     * 从Cookie根据获取值
     * @param request
     * @param name
     * @return
     */
    public static String getValueFromCookies(HttpServletRequest request,String name) {
        String value = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
             for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (cookie.getName().equals(name)) {
                    value = cookie.getValue();
                    break;
                }
            }
        }
        return value;
    }

}
