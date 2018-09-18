package com.mo9.raptor.utils;

import com.mo9.raptor.bean.ReqHeaderParams;

import javax.servlet.http.HttpServletRequest;

/**
 * ip相关工具类
 * @author zma
 * @date 2018/9/18
 */
public class IpUtils {

    public static String getRemoteHost(HttpServletRequest request) {
        String unknown = "unknown";
        String localIp = "0:0:0:0:0:0:0:1";
        String ip = request.getHeader(ReqHeaderParams.X_FORWARDED_FOR);
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader(ReqHeaderParams.PROXY_CLIENT_IP);
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader(ReqHeaderParams.WL_PROXY_CLIENT_IP);
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader(ReqHeaderParams.HTTP_CLIENT_IP);
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader(ReqHeaderParams.X_REAL_IP);
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return localIp.equals(ip) ? "127.0.0.1" : ip;
    }
}
