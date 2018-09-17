package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.enums.CaptchaBusinessEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.redis.RedisParams;
import com.mo9.raptor.redis.RedisServiceApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author zma
 * @date 2018/9/17
 */
@RestController
@RequestMapping(value = "/test")
public class TestController {

    @Resource
    private RedisServiceApi redisServiceApi;

    @Resource(name = "raptorRedis")
    private RedisTemplate raptorRedis;

    @Value("${test.open}")
    private String testOpen = "false";

    /**
     * 获取短信验证码
     * @param request
     * @param receive
     * @return
     */
    @RequestMapping("/fetch_mobile_verification_code")
    public BaseResponse fetchSmsVerificationCode(HttpServletRequest request, @RequestParam("mobile")String receive){
        BaseResponse response = new BaseResponse();
        if(!Boolean.valueOf(testOpen)){
            return response.buildFailureResponse(ResCodeEnum.TEST_OPEN_CLOSE) ;
        }
        String redisKey = getRedisKey(RedisParams.MOBILE_CAPTCHA_KEY, receive, CaptchaBusinessEnum.LOGIN);
        String redisCaptcha = (String) redisServiceApi.get(redisKey, raptorRedis);
        JSONObject codeData = new JSONObject();
        codeData.put("code",redisCaptcha);
        return new BaseResponse("手机验证码",codeData);
    }

    private String getRedisKey(String redisKey, String receive, CaptchaBusinessEnum businessCode){
        return redisKey + businessCode + "_" + receive;
    }
}
