package com.mo9.libracredit.controller;

import com.mo9.libracredit.bean.BaseResponse;
import com.mo9.libracredit.bean.ReqHeaderParams;
import com.mo9.libracredit.bean.req.SendEmailVerificationCodeReq;
import com.mo9.libracredit.bean.req.SendSmsVerificationCodeReq;
import com.mo9.libracredit.bean.req.SendVerficationByTokenReq;
import com.mo9.libracredit.bean.req.SendVerficationByUserReq;
import com.mo9.libracredit.entity.UserEntity;
import com.mo9.libracredit.enums.CaptchaBusinessEnum;
import com.mo9.libracredit.enums.ResCodeEnum;
import com.mo9.libracredit.redis.RedisParams;
import com.mo9.libracredit.redis.RedisServiceApi;
import com.mo9.libracredit.service.CaptchaService;
import com.mo9.libracredit.service.StrategyService;
import com.mo9.libracredit.service.UserService;
import com.mo9.libracredit.util.ValidateGraphicCode;
import com.mo9.libracredit.util.log.Log;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zma
 * @date 2018/7/8
 */
@RestController
@RequestMapping(value = "/api/libra/v2")
public class CaptchaController {
    private static Logger logger = Log.get();

    @Resource
    private RedisServiceApi redisServiceApi;

    @Resource
    private ValidateGraphicCode validateGraphicCode;

    @Resource(name = "libracreditRedis")
    private RedisTemplate libracreditRedis;

    @Resource
    private CaptchaService captchaService;

    @Resource
    private UserService userService;



    /**
     * 发送短信验证码,已登录状态
     * （1）判断ip是否超出频率限制
     * （2）校验邮箱或手机格式合法
     * （3）发送验证码
     *
     * @return
     */
    @RequestMapping(value = "/account/request_me_send_sms_verification_code", method = RequestMethod.POST)
    public BaseResponse<Boolean> sendMeSmsVerificationCode(@RequestBody @Validated SendSmsVerificationCodeReq sendSmsVerificationCodeReq, HttpServletRequest request) {
        BaseResponse<Boolean> response = new BaseResponse<>();
        String language = request.getHeader(ReqHeaderParams.LANGUAGE);
        String mobile = sendSmsVerificationCodeReq.getMobile();
        CaptchaBusinessEnum reason = sendSmsVerificationCodeReq.getReason();
        String internationalCode = sendSmsVerificationCodeReq.getInternationalCode();
        Boolean sendRes = false;
        try{
            //判断ip获取验证码频率是否超出限制（暂定每小时最多获取10次）
            Boolean limitIpRes = captchaService.checkRateLimitIp(request, 3600L, 10);
            if (!limitIpRes) {
                logger.warn("用户在[{}]时发送短信验证码频繁，账号：[{}]", reason.getDesc(), mobile);
                return response.buildFailureResponse(ResCodeEnum.CAPTCHA_GET_TOO_OFTEN);
            }
            //发送短信验证
            ResCodeEnum resCodeEnum = captchaService.sendMobileCaptcha(reason, mobile, internationalCode, language);
            if (ResCodeEnum.SUCCESS.equals(resCodeEnum)) {
                sendRes = true;
            } else {
                response.setCode(resCodeEnum.getCode());
            }
            response.setMessage(resCodeEnum.getMessage());
            response.setData(sendRes);
            return response;
        }catch (Exception e){
            Log.error(logger, e,"已登录状态用户发送手机验证码出现异常mobile={},reason={}",mobile, reason);
            return response.buildFailureResponse(ResCodeEnum.EXCTPION_CODE);
        }
    }

    /**
     * 发送邮箱验证码，已登录状态
     * （1）判断ip是否超出频率限制
     * （2）校验邮箱或手机格式合法
     * （3）发送验证码
     *
     * @return
     */
    @PostMapping(value = "/account/request_send_me_email_verification_code")
    public BaseResponse<Boolean> sendMeEmailVerificationCode(@RequestBody @Validated SendEmailVerificationCodeReq sendEmailVerificationCodeReq, HttpServletRequest request) {
        BaseResponse<Boolean> response = new BaseResponse<>();
        String language = request.getHeader(ReqHeaderParams.LANGUAGE);
        String email = sendEmailVerificationCodeReq.getEmail();
        CaptchaBusinessEnum reason = sendEmailVerificationCodeReq.getReason();
        Boolean sendRes = false;
        try{
            //判断ip获取验证码频率是否超出限制（暂定每小时最多获取10次）
            Boolean limitIpRes = captchaService.checkRateLimitIp(request, 3600L, 10);
            if (!limitIpRes) {
                logger.warn("用户在[{}]时发送短信验证码频繁，账号：[{}]", reason.getDesc(), email);
                return response.buildFailureResponse(ResCodeEnum.CAPTCHA_GET_TOO_OFTEN);
            }
            //发送邮箱验证
            ResCodeEnum resCodeEnum = captchaService.sendEmailCaptcha(reason, email, language);
            if (ResCodeEnum.SUCCESS.equals(resCodeEnum)) {
                sendRes = true;
            } else {
                response.setCode(resCodeEnum.getCode());
            }
            response.setMessage(resCodeEnum.getMessage());
            response.setData(sendRes);
            return response;
        }catch (Exception e){
            Log.error(logger, e,"已登录状态用户发送邮箱验证码出现异常email={},reason={}",email, reason);
            return response.buildFailureResponse(ResCodeEnum.EXCTPION_CODE);
        }

    }

    @PostMapping(value = "/request_send_me_verification_code")
    public BaseResponse<Boolean> sendVerficationByUser(@RequestBody @Validated SendVerficationByUserReq req, HttpServletRequest request){
        BaseResponse<Boolean> response = new BaseResponse<>();
        String language = request.getHeader(ReqHeaderParams.LANGUAGE);
        CaptchaBusinessEnum reason = req.getReason();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        try{
            UserEntity userEntity = userService.findByUserCodeAndIsDeleted(userCode, false);
            if(userEntity == null){
                return response.buildFailureResponse(ResCodeEnum.USER_ACCOUNT_NOT_EXIT_OR_DISABLED);
            }

            //判断ip获取验证码频率是否超出限制（暂定每小时最多获取10次）
            Boolean limitIpRes = captchaService.checkRateLimitIp(request, 3600L, 10);
            if (!limitIpRes) {
                logger.warn("用户在[{}]时发送短信验证码频繁，账号：[{}]", reason.getDesc(), userCode);
                return response.buildFailureResponse(ResCodeEnum.CAPTCHA_GET_TOO_OFTEN);
            }

            ResCodeEnum resCodeEnum = captchaService.sendCaptchaByUserEntity(reason, userEntity, language);
            if(resCodeEnum != ResCodeEnum.SUCCESS){
                return response.buildFailureResponse(resCodeEnum);
            }
            return response.buildSuccessResponse(true);
        }catch (Exception e){
            Log.error(logger, e,"根据token向用户发送验证码出现异常userCode={},reason={}", userCode, reason);
            return response.buildFailureResponse(ResCodeEnum.EXCTPION_CODE);
        }
    }

    /**
     * 获取图形验证码
     *
     * @param request
     * @param captchaKey
     * @throws IOException
     */
    @RequestMapping(value = "/get_graphic_code")
    public void getGraphicCode(HttpServletResponse request, @RequestParam("userCode") String captchaKey) throws IOException {

        //生成图形验证码
        validateGraphicCode.createCode();
        String code = validateGraphicCode.getCode();
        // 存储到redis
        redisServiceApi.set(RedisParams.GRAPHIC_CAPTCHA_KEY + captchaKey, code, RedisParams.EXPIRE_5M, libracreditRedis);
        logger.info("图片验证码: [{}]", code);
        // 禁止图像缓存。
        request.setHeader("Pragma", "no-cache");
        request.setHeader("Cache-Control", "no-cache");
        request.setDateHeader("Expires", 0);
        //设置响应图片格式
        request.setContentType("image/png");
        // 将图像输出到Servlet输出流中。
        ImageIO.write(validateGraphicCode.getBuffImg(), "png", request.getOutputStream());
    }

    /**
     * 验证图形验证码
     *
     * @param graphicKey
     * @param captcha
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/check_graphic_code")
    @ResponseBody
    public BaseResponse<Boolean> checkGraphicCode(@RequestParam("userCode") String graphicKey, @RequestParam("captcha") String captcha) throws IOException {
        BaseResponse<Boolean> response = new BaseResponse<>();
        try {
            response = checkCaptcha(RedisParams.GRAPHIC_CAPTCHA_KEY + graphicKey, captcha, response);
        } catch (Exception e) {
            Log.error(logger, e,"图形验证发生异常{}");

        }
        return response;
    }

    /**
     * 校验验证码是否输入正确
     *
     * @param redisCaptchaKey 验证码在redis存储的key
     * @param captcha
     * @return
     */
    private BaseResponse<Boolean> checkCaptcha(String redisCaptchaKey, String captcha, BaseResponse<Boolean> response) throws Exception {

        Boolean result = false;

        String pinCode = (String) redisServiceApi.get(redisCaptchaKey, libracreditRedis);

        if (StringUtils.isBlank(pinCode)) {
            response.setData(result);
            response.setMessage("验证码已失效");
            return response;
        }

        if (!captcha.equals(pinCode)) {
            response.setMessage("验证码错误");
        } else {
            result = true;
            response.setMessage("ok");
        }
        response.setData(result);
        redisServiceApi.remove(redisCaptchaKey, libracreditRedis);
        return response;
    }
}

