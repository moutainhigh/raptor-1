package com.mo9.raptor.intercepter;

import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.redis.RedisParams;
import com.mo9.raptor.redis.RedisServiceApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 下单拦截器
 * Created by xzhang on 2018/10/10.
 */
@Component("addLoanOrderInterceptor")
@EnableAutoConfiguration
public class AddLoanOrderInterceptor extends HandlerInterceptorAdapter {

    private static Logger logger = LoggerFactory.getLogger(AddLoanOrderInterceptor.class);
    @Resource
    private RedisServiceApi redisServiceApi;

    @Resource(name = "raptorRedis")
    private RedisTemplate raptorRedis;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        String clientId = request.getHeader(ReqHeaderParams.CLIENT_ID);

        Object counts = redisServiceApi.get(RedisParams.ADD_LOAN_ORDER_KEY + userCode, raptorRedis);
        Long newCounts = null;
        if (counts != null) {
            // 增加下单次数
            newCounts = redisServiceApi.increment(RedisParams.ADD_LOAN_ORDER_KEY + userCode, 1L, raptorRedis);
        } else {
            // 60秒内没有下过单, 则设置下过一次单
            newCounts = 1L;
            redisServiceApi.set(RedisParams.ADD_LOAN_ORDER_KEY + userCode, newCounts, 60L, raptorRedis);
        }

        if (newCounts == 11L) {
            redisServiceApi.remove(RedisParams.getAccessToken(clientId,userCode), raptorRedis);
            logger.warn("用户[{}]在一分钟内第[{}]次调用下单接口, 被踢下线", userCode, newCounts);
            return false;
        }
        return true;
    }
}
