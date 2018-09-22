package com.mo9.raptor.controller;

import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.vo.SendSmsVerificationCodeReq;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.CaptchaBusinessEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.redis.RedisParams;
import com.mo9.raptor.redis.RedisServiceApi;
import com.mo9.raptor.service.CaptchaService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.ValidateGraphicCode;
import com.mo9.raptor.utils.log.Log;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/auth")
public class CaptchaController {
    private static Logger logger = Log.get();

    @Autowired
    UserService userService;

    @Resource
    private RedisServiceApi redisServiceApi;

    @Resource
    private ValidateGraphicCode validateGraphicCode;

    @Resource(name = "raptorRedis")
    private RedisTemplate raptorRedis;

    @Resource
    private CaptchaService captchaService;


    /**
     * 发送登录短信验证码
     * （1）判断ip是否超出频率限制
     * （2）校验邮箱或手机格式合法
     * （3）发送验证码
     *
     * @return
     */
    @RequestMapping(value = "/send_login_code", method = RequestMethod.POST)
    public BaseResponse<Boolean> sendMeSmsVerificationCode(@RequestBody @Validated SendSmsVerificationCodeReq sendSmsVerificationCodeReq, HttpServletRequest request) {
        BaseResponse<Boolean> response = new BaseResponse<>();
        String mobile = sendSmsVerificationCodeReq.getMobile();
        CaptchaBusinessEnum reason = CaptchaBusinessEnum.LOGIN;
        try{
            /**   逻辑修缮 by James 18/09/16    */
            //这里就要判断用户是否再名单之列
            UserEntity user = userService.findByMobileAndDeleted(mobile,false);
            if(null == user){
                logger.warn("发送登录验证码-------->>>>>>>>非白名单用户[{}]", mobile);
                return response.buildFailureResponse(ResCodeEnum.NOT_WHITE_LIST_USER);
            }
            Boolean sendRes = false;
            //判断ip获取验证码频率是否超出限制（暂定每小时最多获取10次）
            Boolean limitIpRes = captchaService.checkRateLimitIp(request, 3600L, 10);
            if (!limitIpRes) {
                logger.warn("用户在[{}]时发送短信验证码频繁，账号：[{}]", reason.getDesc(), mobile);
                return response.buildFailureResponse(ResCodeEnum.CAPTCHA_GET_TOO_OFTEN);
            }
            //发送短信验证
            ResCodeEnum resCodeEnum = captchaService.sendMobileCaptchaCN(reason, mobile);
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
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
    }


    /**
     * 获取图形验证码
     *
     * @param request
     * @param captchaKey
     * @throws IOException
     */
    @RequestMapping(value = "/send_graph_code")
    public void getGraphicCode(HttpServletResponse request, @RequestParam("userCode") String captchaKey) throws IOException {

        try{
            //生成图形验证码
            validateGraphicCode.createCode();
            String code = validateGraphicCode.getCode();
            // 存储到redis
            redisServiceApi.set(RedisParams.GRAPHIC_CAPTCHA_KEY + captchaKey, code, RedisParams.EXPIRE_5M, raptorRedis);
            logger.info("图片验证码: [{}]", code);
            // 禁止图像缓存。
            request.setHeader("Pragma", "no-cache");
            request.setHeader("Cache-Control", "no-cache");
            request.setDateHeader("Expires", 0);
            //设置响应图片格式
            request.setContentType("image/png");
            // 将图像输出到Servlet输出流中。
            ImageIO.write(validateGraphicCode.getBuffImg(), "png", request.getOutputStream());
        }catch (Exception e){
            Log.error(logger, e,"生成图形验证码出现异常");
        }

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
            Log.error(logger, e,"图形验证发生异常");

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

        String pinCode = (String) redisServiceApi.get(redisCaptchaKey, raptorRedis);

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
        redisServiceApi.remove(redisCaptchaKey, raptorRedis);
        return response;
    }
}

