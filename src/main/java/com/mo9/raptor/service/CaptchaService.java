package com.mo9.raptor.service;


import com.mo9.raptor.enums.CaptchaBusinessEnum;
import com.mo9.raptor.enums.CaptchaTypeEnum;
import com.mo9.raptor.enums.ResCodeEnum;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 验证码相关服务
 * @author zma
 * @date 2018/7/5
 */
public interface CaptchaService {



    /**
     * 判断ip发送消息频率是否超过限制
     * @param request
     * @param limitSecond 受限制的时间（秒）
     * @param limitCount  限制时间内最大次数
     * @return
     */
    Boolean checkRateLimitIp(HttpServletRequest request, Long limitSecond, Integer limitCount);


    /**
     * 发送手机验证码
     * @param businessCode 验证码用途
     * @param mobile
     * @return
     */
    ResCodeEnum sendMobileCaptchaCN(CaptchaBusinessEnum businessCode, String mobile);




    /**
     * 校验验证码是否正确,当为邮箱验证和手机验证时，receive为邮箱或手机号，当为谷歌验证时receive为userCode，当为行为验证时receive为token
     * @param captchaType 验证码类型
     * @param businessCode 验证码用途
     * @param receive 接收者
     * @param captcha 验证码
     * @param isClearCaptcha 是否清除验证码缓存
     * @return
     */
    ResCodeEnum checkCaptcha(CaptchaTypeEnum captchaType, CaptchaBusinessEnum businessCode, String receive, String captcha, boolean isClearCaptcha) throws IOException;


    /**
     * 检验验证码校验频率,目前规则定义1min只允许校验10次
     * @param receive
     * @param reason
     * @return
     */
    boolean checkCaptchaLimit(String receive, CaptchaBusinessEnum reason);
}
