package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.CaptchaBusinessEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.redis.RedisParams;
import com.mo9.raptor.redis.RedisServiceApi;
import com.mo9.raptor.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static Logger logger = LoggerFactory.getLogger(TestController.class);

    @Resource
    private RedisServiceApi redisServiceApi;

    @Resource
    private UserService userService;

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

    /**
     * 修改用户状态
     */
    @RequestMapping("/update_user_status")
    public BaseResponse<UserEntity> updateUserStatus(HttpServletRequest request, @RequestParam("status") StatusEnum status){
        BaseResponse<UserEntity> response = new BaseResponse<UserEntity>();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        try{
            if(!Boolean.valueOf(testOpen)){
                return response.buildFailureResponse(ResCodeEnum.TEST_OPEN_CLOSE) ;
            }
            UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
            userEntity.setStatus(status.name());
            userEntity.setRemark("测试接口修改用户状态为:" + status.name());
            userService.save(userEntity);
            logger.info("测试修改用户状态为status={}", status);
            return response.buildSuccessResponse(userEntity);
        }catch (Exception e){
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
    }

    private String getRedisKey(String redisKey, String receive, CaptchaBusinessEnum businessCode){
        return redisKey + businessCode + "_" + receive;
    }
}
