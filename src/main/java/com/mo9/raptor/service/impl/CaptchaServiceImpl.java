package com.mo9.libracredit.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mo9.libracredit.bean.ReqHeaderParams;
import com.mo9.libracredit.bean.vo.GoogleAuthVo;
import com.mo9.libracredit.entity.UserEntity;
import com.mo9.libracredit.enums.*;
import com.mo9.libracredit.redis.RedisParams;
import com.mo9.libracredit.redis.RedisServiceApi;
import com.mo9.libracredit.service.CaptchaService;
import com.mo9.libracredit.service.UserService;
import com.mo9.libracredit.util.MessageSend;
import com.mo9.libracredit.util.RandomUtils;
import com.mo9.libracredit.util.httpclient.HttpClientApi;
import com.mo9.libracredit.util.httpclient.bean.HttpResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.mo9.libracredit.bean.MessageVariable.*;
import static com.mo9.libracredit.enums.MessageNotifyEventEnum.GENERAL_CAPTCHA;
import static com.mo9.libracredit.enums.MessageNotifyEventEnum.resetTradePasswordCaptcha;

/**
 * 验证码相关服务
 *
 * @author zma
 * @date 2018/7/5
 */
@Service(value = "captchaService")
public class CaptchaServiceImpl implements CaptchaService {
    private static final Logger logger = LoggerFactory.getLogger(CaptchaServiceImpl.class);

    @Autowired
    private HttpClientApi httpClientApi;

    @Value("${suona.google.binding.url}")
    private String googleBindingUrl;

    @Value("${suona.google.check.url}")
    private String googleCheckUrl;

    @Resource
    private RedisServiceApi redisServiceApi;

    @Resource(name = "libracreditRedis")
    private RedisTemplate redisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private MessageSend messageSend;

    @Value(value = "${test.open}")
    private String testOpen;



    @Override
    public ResCodeEnum checkGoogleCaptcha(String captcha, CaptchaBusinessEnum businessCode, String userCode) throws IOException {
        String token = null;
        String successCode = "0";
        // 发送谷歌验证请求
        JSONObject params = new JSONObject();
        params.put("userCode", userCode);
        params.put("systemCode", SYSTEM_CODE);
        params.put("authCode", captcha);
        HttpResult httpResult = httpClientApi.doPostJson(googleCheckUrl, params.toJSONString());
        JSONObject resultData = JSON.parseObject(httpResult.getData());
        String code = resultData.getString("code");
        if (successCode.equals(code)) {
            Boolean checkResult = resultData.getBoolean("data");
            if (checkResult) {
                return ResCodeEnum.SUCCESS;
            }
        }
        return ResCodeEnum.CAPTCHA_CHECK_ERROR;
    }

    @Override
    public Boolean checkRateLimitIp(HttpServletRequest request, Long limitSecond, Integer limitCount) {

        Boolean aBoolean = Boolean.valueOf(testOpen);
        if(aBoolean){
            return true;
        }
        //获取真实ip
        String clientIp = getRemoteHost(request);
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
    public GoogleAuthVo bindingGoogleAuth(String userCode) throws IOException {
        String successCode = "0";
        // 发送谷歌验证请求
        JSONObject params = new JSONObject();
        params.put("userCode", userCode);
        params.put("systemCode", SYSTEM_CODE);
        params.put("userName", "userName");
        params.put("issuer", "LibraCredit");
        HttpResult httpResult = httpClientApi.doPostJson(googleBindingUrl, params.toJSONString());
        JSONObject resultData = JSON.parseObject(httpResult.getData());
        String code = resultData.getString("code");
        if (successCode.equals(code)) {
            String dataJson = resultData.getString("data");
            GoogleAuthVo googleAuthVo = JSON.parseObject(dataJson, GoogleAuthVo.class);
            return googleAuthVo;
        }
        return null;
    }

    @Override
    public ResCodeEnum sendCaptcha(CaptchaTypeEnum captchaType, CaptchaBusinessEnum businessCode, String receive, String internationalCode, String language) {
        if (captchaType == CaptchaTypeEnum.EMAIL) {
            return sendEmailCaptcha(businessCode, receive, language);
        } else if (captchaType == CaptchaTypeEnum.MOBILE) {
            return sendMobileCaptcha(businessCode, receive, internationalCode, language);
        } else {
            return ResCodeEnum.NOT_SUPPORT_CAPTCHA_TYPE;
        }
    }

    @Override
    public ResCodeEnum sendMobileCaptcha(CaptchaBusinessEnum businessCode, String mobile, String internationalCode, String language) {
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
    public ResCodeEnum sendEmailCaptcha(CaptchaBusinessEnum businessCode, String email, String language) {
        AreaCodeEnum areaCodeEnum = AreaCodeEnum.getAreaCodeBuyLanguage(language);
        String key = getRedisKey(RedisParams.EMAIL_CAPTCHA_KEY, email, businessCode);
        String pinCode = (String) redisServiceApi.get(key, redisTemplate);
        //验证码
        if (StringUtils.isBlank(pinCode)) {
            pinCode = RandomUtils.generateNumString(6);
            redisServiceApi.set(key, pinCode, businessCode.getExpireTime(), redisTemplate);
        }
        //30s内同一帐号只可获取一次验证码
        boolean existence = redisServiceApi.exists(RedisParams.LIMIT_CAPTCHA_KEY + email, redisTemplate);
        if (!existence) {
            redisServiceApi.set(RedisParams.LIMIT_CAPTCHA_KEY + email, email, RedisParams.EXPIRE_30S, redisTemplate);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(CAPTCHA, pinCode);
            MessageNotifyEventEnum messageNotifyEvent = getMessageNotifyEventEnum(businessCode);
            messageSend.sendMail(email, messageNotifyEvent, areaCodeEnum, jsonObject);
            logger.info("验证码发送成功，receive={},businessCode={},captcha={}", email, businessCode, pinCode);
        } else {
            logger.warn("发送邮箱验证码过于频繁，receive={},businessCode={}", email, businessCode);
            return ResCodeEnum.CAPTCHA_GET_TOO_OFTEN;
        }
        return ResCodeEnum.SUCCESS;
    }

    @Override
    public ResCodeEnum sendCaptchaByUserCode(CaptchaBusinessEnum businessCode, String userCode, String language) {
        UserEntity userEntity = userService.findByUserCodeAndIsDeleted(userCode, false);
        if (userEntity == null) {
            return ResCodeEnum.USER_ACCOUNT_NOT_EXIT_OR_DISABLED;
        }
        return sendCaptchaByUserEntity(businessCode, userEntity, language);
    }

    @Override
    public ResCodeEnum sendCaptchaByUserEntity(CaptchaBusinessEnum businessCode, UserEntity userEntity, String language) {
        String mobile = userEntity.getMobile();
        String email = userEntity.getEmail();
        Boolean verifyGoogle = userEntity.getVerifyGoogle();
        String bindingAreaCode = userEntity.getBindingAreaCode();
        if(StringUtils.isBlank(language)){
            language = UserEntity.getLanguage(userEntity);
        }
        if (verifyGoogle) {
            return ResCodeEnum.SUCCESS;
        }
        AccountMethodEnum registerMethod = userEntity.getRegisterMethod();
        if (registerMethod == AccountMethodEnum.EMAIL) {
            return sendEmailCaptcha(businessCode, email, language);
        } else {
            return sendMobileCaptcha(businessCode, mobile, bindingAreaCode, language);
        }
    }

    @Override
    public ResCodeEnum checkCaptcha(CaptchaTypeEnum captchaType, CaptchaBusinessEnum businessCode, String receive, String captcha, boolean isClearCaptcha) throws IOException {
        String key = null;
        if (captchaType == CaptchaTypeEnum.MOBILE) {
            key = getRedisKey(RedisParams.MOBILE_CAPTCHA_KEY, receive, businessCode);
        } else if (captchaType == CaptchaTypeEnum.EMAIL) {
            key = getRedisKey(RedisParams.EMAIL_CAPTCHA_KEY, receive, businessCode);
        } else if (captchaType == CaptchaTypeEnum.GOOGLE) {
            return checkGoogleCaptcha(captcha, businessCode, receive);
//            return ResCodeEnum.SUCCESS;
        } else if (captchaType == CaptchaTypeEnum.BEHAVIOR) {
            //TODO ukar 行为验证待实现
            return ResCodeEnum.SUCCESS;
        } else {
            return ResCodeEnum.NOT_SUPPORT_CAPTCHA_TYPE;
        }
        String redisCaptcha = (String) redisServiceApi.get(key, redisTemplate);
        if (StringUtils.isBlank(redisCaptcha)) {
            return ResCodeEnum.CAPTCHA_IS_INVALID;
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
    public ResCodeEnum checkCaptcha(UserEntity userEntity, CaptchaBusinessEnum businessCode, String captcha, boolean isClearCaptcha) throws IOException {
        String receive = null;
        String key = null;
        Boolean verifyGoogle = userEntity.getVerifyGoogle();
        AccountMethodEnum registerMethod = userEntity.getRegisterMethod();
        if (verifyGoogle) {
            return checkGoogleCaptcha(captcha, businessCode, userEntity.getUserCode());
        } else {
            if (registerMethod == AccountMethodEnum.MOBILE) {
                receive = userEntity.getMobile();
                key = getRedisKey(RedisParams.MOBILE_CAPTCHA_KEY, receive, businessCode);
            }
            if (registerMethod == AccountMethodEnum.EMAIL) {
                receive = userEntity.getEmail();
                key = getRedisKey(RedisParams.EMAIL_CAPTCHA_KEY, receive, businessCode);
            }
        }
        String redisCaptcha = (String) redisServiceApi.get(key, redisTemplate);
        if (StringUtils.isBlank(redisCaptcha)) {
            return ResCodeEnum.CAPTCHA_IS_INVALID;
        }
        if (!redisCaptcha.equals(captcha)) {
            logger.warn("用户验证码校验失败，userCode={}，redisCaptcha={}，captcha={}", userEntity.getUserCode(), redisCaptcha, captcha);
            return ResCodeEnum.CAPTCHA_CHECK_ERROR;
        }
        if(isClearCaptcha){
            redisServiceApi.remove(key, redisTemplate);
        }
        return ResCodeEnum.SUCCESS;
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

    private String getRemoteHost(HttpServletRequest request) {
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
            case REGISTER:
                messageNotifyEvent = MessageNotifyEventEnum.registerCaptcha;
                break;
            case LOGIN:
                messageNotifyEvent = MessageNotifyEventEnum.loginCaptcha;
                break;
            case RESET_DEAL_PASSWORD:
                messageNotifyEvent = MessageNotifyEventEnum.setTradePasswordCaptcha;
                break;
            case RESET_PASSWORD:
                messageNotifyEvent = MessageNotifyEventEnum.forgetPasswordCaptcha;
                break;
            case BINDING_MOBILE:
                messageNotifyEvent = MessageNotifyEventEnum.bindingMobileCaptcha;
                break;
            case BINDING_EMAIL:
                messageNotifyEvent = MessageNotifyEventEnum.bindingEmailCaptcha;
                break;
            default:
                messageNotifyEvent = MessageNotifyEventEnum.GENERAL_CAPTCHA;
        }
        return messageNotifyEvent;
    }

}
