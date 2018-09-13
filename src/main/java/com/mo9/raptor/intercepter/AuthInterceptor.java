package com.mo9.raptor.intercepter;

import com.mo9.raptor.redis.RedisParams;
import com.mo9.raptor.redis.RedisServiceApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @author zma
 * @date 2018/9/13
 */

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    private static Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
    @Resource
    private RedisServiceApi redisServiceApi;

    @Resource(name = "raptorRedis")
    private RedisTemplate raptorRedis;

    private String[] excludeUrls = new String[0];

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //检查是否在白名单
        String uri = request.getRequestURI();
        if(isExclude(uri)) {
            logger.debug("白名单地址，自动跳过拦截器[{}]",uri);
            return true;
        }
        //须登录，检查是否在登录状态 延长登录时间
        String accountMobile = getAccountMobile(request);
        String accessToken = getAccessToken(request);
        if (!StringUtils.isEmpty(accountMobile) && !StringUtils.isEmpty(accessToken)) {
            String clientId = getClientId(request);
            if (!StringUtils.isEmpty(clientId)){
                httpError(response);
                return false;
            }
            Boolean isLogin = validateLoginStatus(accountMobile,accessToken,clientId);
            if (!isLogin){
                httpError(response);
                return false;
            }
        }else {
            httpError(response);
            return false;
        }
        return true;
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
            //过期时间小于1分钟的,更新token
            if(expire < RedisParams.EXPIRE_1M) {
                //TODO 根据clientId 对pc和app进行区分
                redisServiceApi.expireSeconds(RedisParams.getAccessToken(clientId,accountMobile), RedisParams.EXPIRE_1D,raptorRedis);
            }
        }
        return true;
    }


    public String getAccountMobile(HttpServletRequest request) {
        return request.getHeader("Account-Mobile");
    }
    public String getAccessToken(HttpServletRequest request) {
        return request.getHeader("Access-Token");
    }

    public String getClientId(HttpServletRequest request) {
        return request.getHeader("Client-Id");
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
        return Arrays.asList(excludeUrls).contains(uri);
    }

}
