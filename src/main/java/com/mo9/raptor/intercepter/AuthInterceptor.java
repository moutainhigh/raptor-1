package com.mo9.raptor.intercepter;

import com.mo9.raptor.redis.RedisParams;
import com.mo9.raptor.redis.RedisServiceApi;
import com.mo9.raptor.utils.CommonValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;

/**
 * @author zma
 * @date 2018/9/13
 */

@Component
@EnableAutoConfiguration
public class AuthInterceptor extends HandlerInterceptorAdapter {

    private static Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
    @Resource
    private RedisServiceApi redisServiceApi;

    @Resource(name = "raptorRedis")
    private RedisTemplate raptorRedis;

    @Value("${system.switch}")
    private String systemSwitch ;

    @Value("${system.clientVersion}")
    private Integer systemClientVersion = 0;

    @Value("${raptor.exclude.urls}")
    private String[] excludeUrls = new String[0];

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //检查是否在白名单
        String uri = request.getRequestURI();
        if(isExclude(uri)) {
            logger.debug("白名单地址，自动跳过拦截器[{}]",uri);
            return true;
        }
        if(systemSwitch == null || !(CommonValues.TRUE.equals(systemSwitch))){
            logger.error("系统已经关闭" + new Date());
            return false;
        }
        //须登录，检查是否在登录状态 延长登录时间
        String accountCode = getAccountCode(request);
        String accessToken = getAccessToken(request);
        if (!StringUtils.isEmpty(accountCode) && !StringUtils.isEmpty(accessToken)) {
            String clientId = getClientId(request);
            if (StringUtils.isEmpty(clientId)){
                httpError(response);
                return false;
            }
            Boolean isLogin = validateLoginStatus(accountCode,accessToken,clientId);
            if (!isLogin){
                response.setStatus(401);
                return false;
            }
            //校验客户端版本
            Boolean version = validateClientVersion(request);
            if (!version){
                response.setStatus(480);
                return false;
            }


        }else {
            httpError(response);
            return false;
        }
        return true;
    }

    /**
     * 校验客户端版本
     * @param request
     * @return
     */
    private Boolean validateClientVersion(HttpServletRequest request) {
        //安卓不判断，客户端标识 安卓901 苹果902
        String clientId = getClientId(request);
        if ("901".equals(clientId)){
            return true;
        }
        String clientVersion = getClientVersion(request);
        return Integer.valueOf(clientVersion) >= systemClientVersion;
    }
    /**
     * 检查是否在登录状态，延长登录时间
     * @param accountMobile
     * @param accessToken
     * @param clientId
     * @return
     */
    private Boolean validateLoginStatus(String accountMobile,String accessToken,String clientId) {
        Object preAccessToken = redisServiceApi.get(RedisParams.getAccessToken(clientId,accountMobile), raptorRedis);
        if (!accessToken.equals(preAccessToken)){
            return false;
        }else {
            Long expire = redisServiceApi.getExpireSeconds(RedisParams.getAccessToken(clientId,accountMobile),raptorRedis);
            //过期时间小于29天的,更新token，app登录30天过期
            if(expire < RedisParams.EXPIRE_29D) {
                redisServiceApi.expireSeconds(RedisParams.getAccessToken(clientId,accountMobile), RedisParams.EXPIRE_30D,raptorRedis);
            }
        }
        return true;
    }


    public String getAccountCode(HttpServletRequest request) {
        return request.getHeader("Account-Code");
    }
    public String getAccessToken(HttpServletRequest request) {
        return request.getHeader("Access-Token");
    }

    public String getClientId(HttpServletRequest request) {
        return request.getHeader("Client-Id");
    }
    public String getClientVersion(HttpServletRequest request) {
        return request.getHeader("Client-Version");
    }
    public void httpError(HttpServletResponse response) {
        response.setStatus(403);
    }
    /**
     * 检查uri是否在白名单中
     * @param uri
     * @return
     */
    private boolean isExclude(String uri) {
        for (String urlPattern: excludeUrls){
            if (urlPattern.contains("*")){
                String subUrl = urlPattern.substring(0,urlPattern.indexOf("*"));
                if (uri.startsWith(subUrl)){
                    return true;
                }
            }else {
                if (uri.equals(urlPattern)){
                    return true;
                }
            }
        }
        return false;
    }

}
