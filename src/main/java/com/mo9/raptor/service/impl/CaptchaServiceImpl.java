package com.mo9.raptor.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.enums.*;
import com.mo9.raptor.redis.RedisParams;
import com.mo9.raptor.redis.RedisServiceApi;
import com.mo9.raptor.service.CaptchaService;
import com.mo9.raptor.utils.IpUtils;
import com.mo9.raptor.utils.MessageSend;
import com.mo9.raptor.utils.RandomUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.mo9.raptor.bean.MessageVariable.CAPTCHA;
import static com.mo9.raptor.bean.MessageVariable.RAPTOR_SIGN_NAME;
import static com.mo9.raptor.bean.MessageVariable.SIGN;


/**
 * 验证码相关服务
 *
 * @author zma
 * @date 2018/7/5
 */
@Service(value = "captchaService")
public class CaptchaServiceImpl implements CaptchaService {
    private static final Logger logger = LoggerFactory.getLogger(CaptchaServiceImpl.class);
    @Resource
    private RedisServiceApi redisServiceApi;

    @Resource(name = "raptorRedis")
    private RedisTemplate redisTemplate;


    @Resource
    private MessageSend messageSend;

    @Value(value = "${test.open}")
    private String testOpen;




    @Override
    public Boolean checkRateLimitIp(HttpServletRequest request, Long limitSecond, Integer limitCount) {

        Boolean aBoolean = Boolean.valueOf(testOpen);
        if(aBoolean){
            return true;
        }
        //获取真实ip
        String clientIp = IpUtils.getRemoteHost(request);
        logger.info("ip访问频率限制，获取到客户端真实ip={}", clientIp);
        //过滤掉公司ip限制
        List<String> ips = Arrays.asList("127.0.0.1", "192.168.3.31", "180.169.230.186", "192.168.12.52", "192.168.12.118");
        if (ips.contains(clientIp)) {
            return true;
        }

        Object count = redisServiceApi.get(RedisParams.LIMIT_IP_RATES + clientIp, redisTemplate);
        Long ipTimes;
        // 没有此ip 设置一次
        if (count == null) {
            ipTimes = 1L;
            redisServiceApi.set(RedisParams.LIMIT_IP_RATES + clientIp, 1, limitSecond, redisTemplate);
        } else {
            Long increment = redisServiceApi.increment(RedisParams.LIMIT_IP_RATES + clientIp, 1L, redisTemplate);
            ipTimes = increment;
        }
        logger.info("ip:[{}],{}秒内允许访问{}次,第{}次访问", clientIp, limitSecond, limitCount, ipTimes);
        if (ipTimes > limitCount) {
            logger.warn("ip：[{}]受限，超出{}秒最多访问[{}]次的限制，实际访问{}次", clientIp, limitSecond, limitCount, ipTimes);
            return false;
        }
        return true;
    }


    @Override
    public ResCodeEnum sendMobileCaptchaCN(CaptchaBusinessEnum businessCode, String mobile) {
        String key = getRedisKey(RedisParams.MOBILE_CAPTCHA_KEY, mobile, businessCode);
        String pinCode = (String) redisServiceApi.get(key, redisTemplate);
        //验证码
        if (StringUtils.isBlank(pinCode)) {
            pinCode = RandomUtils.generateNumString(6);
            redisServiceApi.set(key, pinCode, businessCode.getExpireTime(), redisTemplate);
        }
        //30s内同一帐号只可获取一次验证码
        boolean existence = redisServiceApi.exists(RedisParams.LIMIT_CAPTCHA_KEY + mobile, redisTemplate);
        if (!existence) {
            redisServiceApi.set(RedisParams.LIMIT_CAPTCHA_KEY + mobile, mobile, RedisParams.EXPIRE_30S, redisTemplate);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(CAPTCHA, pinCode);
            jsonObject.put(SIGN, RAPTOR_SIGN_NAME);
            MessageNotifyEventEnum messageNotifyEvent = getMessageNotifyEventEnum(businessCode);
            messageSend.sendMobileSms(mobile, messageNotifyEvent, AreaCodeEnum.CN, jsonObject);

            logger.info("验证码发送成功，receive={},businessCode={},captcha={}", mobile, businessCode, pinCode);
        } else {
            logger.warn("发送手机验证码过于频繁，receive={},businessCode={}", mobile, businessCode);
            return ResCodeEnum.CAPTCHA_GET_TOO_OFTEN;
        }
        return ResCodeEnum.SUCCESS;
    }



    @Override
    public ResCodeEnum checkCaptcha(CaptchaTypeEnum captchaType, CaptchaBusinessEnum businessCode, String receive, String captcha, boolean isClearCaptcha) throws IOException {
        String key = getRedisKey(RedisParams.MOBILE_CAPTCHA_KEY, receive, businessCode);
        String redisCaptcha = (String) redisServiceApi.get(key, redisTemplate);
        if (StringUtils.isBlank(redisCaptcha)) {
            return ResCodeEnum.CAPTCHA_IS_INVALID;
        }
        //TODO 验证码0000 则验证通过，上线须删除
        if ("0000".equals(captcha)){
            if(isClearCaptcha){
                redisServiceApi.remove(key, redisTemplate);
            }
            return ResCodeEnum.SUCCESS;
        }

        if (!redisCaptcha.equals(captcha)) {
            return ResCodeEnum.CAPTCHA_CHECK_ERROR;
        }
        if(isClearCaptcha){
            redisServiceApi.remove(key, redisTemplate);
        }
        return ResCodeEnum.SUCCESS;
    }

    @Override
    public ResCodeEnum checkLoginMobileCaptcha(String receive, String captcha) throws IOException {
        return checkCaptcha(CaptchaTypeEnum.MOBILE,CaptchaBusinessEnum.LOGIN,receive,captcha,true);
    }


    @Override
    public boolean checkCaptchaLimit(String receive, CaptchaBusinessEnum reason) {
        Integer num = (Integer) redisServiceApi.get(RedisParams.CHECK_CAPTCHA_LIMIT_TIMES, redisTemplate);
        if(num == null){
            num = 1;
            redisServiceApi.set(RedisParams.CHECK_CAPTCHA_LIMIT_TIMES, num, RedisParams.EXPIRE_1M, redisTemplate);
            return true;
        }
        if(num > 10){
            return false;
        }
        redisServiceApi.increment(RedisParams.CHECK_CAPTCHA_LIMIT_TIMES, 1L, redisTemplate);
        return true;
    }



    private String getRedisKey(String redisKey, String receive, CaptchaBusinessEnum businessCode) {
        return redisKey + businessCode + "_" + receive;
    }

    /**
     * 根据不同的发送验证码业务，选择事件
     *
     * @param businessCode
     * @return
     */
    private MessageNotifyEventEnum getMessageNotifyEventEnum(CaptchaBusinessEnum businessCode) {
        MessageNotifyEventEnum messageNotifyEvent;
        switch (businessCode) {
            case LOGIN:
                messageNotifyEvent = MessageNotifyEventEnum.loginCaptcha;
                break;
            default:
                messageNotifyEvent = MessageNotifyEventEnum.GENERAL_CAPTCHA;
        }
        return messageNotifyEvent;
    }

}
