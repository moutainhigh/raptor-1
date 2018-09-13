package com.mo9.libracredit.service;

import com.mo9.libracredit.bean.vo.GoogleAuthVo;
import com.mo9.libracredit.entity.UserEntity;
import com.mo9.libracredit.enums.CaptchaBusinessEnum;
import com.mo9.libracredit.enums.CaptchaTypeEnum;
import com.mo9.libracredit.enums.ResCodeEnum;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 验证码相关服务
 * @author zma
 * @date 2018/7/5
 */
public interface CaptchaService {

    /**
     * 检查谷歌验证码
     * @param captcha 验证码
     * @param businessCode 业务码
     * @param userCode
     * @return
     */
    ResCodeEnum checkGoogleCaptcha(String captcha,CaptchaBusinessEnum businessCode,String userCode) throws IOException;


    /**
     * 判断ip发送消息频率是否超过限制
     * @param request
     * @param limitSecond 受限制的时间（秒）
     * @param limitCount  限制时间内最大次数
     * @return
     */
    Boolean checkRateLimitIp(HttpServletRequest request, Long limitSecond, Integer limitCount);

    /**
     * 绑定谷歌验证服务
     * @param userCode
     * @return
     * @throws IOException
     */
    GoogleAuthVo bindingGoogleAuth(String userCode) throws IOException;

    /**
     * 发送验证码
     * @param captchaType 验证码类型
     * @param businessCode 验证码用途
     * @param receive 接收方
     * @param internationalCode 手机区号，如果是手机验证码必须存在
     * @return
     */
    ResCodeEnum sendCaptcha(CaptchaTypeEnum captchaType, CaptchaBusinessEnum businessCode,String receive, String internationalCode, String language);

    /**
     * 发送手机验证码
     * @param businessCode 验证码用途
     * @param mobile
     * @param internationalCode 手机区号
     * @return
     */
    ResCodeEnum sendMobileCaptcha(CaptchaBusinessEnum businessCode, String mobile, String internationalCode, String language);

    /**
     * 发送邮箱验证码
     * @param businessCode 验证码用途
     * @param email
     * @return
     */
    ResCodeEnum sendEmailCaptcha(CaptchaBusinessEnum businessCode, String email, String language);

    /**
     * 根据userCode 查询用户信息，获取用户注册方式，发送对应的验证码，手机验证码或邮箱验证码
     * @param userCode
     * @param businessCode 验证码用途
     * @return
     */
    ResCodeEnum sendCaptchaByUserCode(CaptchaBusinessEnum businessCode, String userCode, String language);

    /**
     * 根据userEntity 获取用户注册方式，发送对应的验证码，手机验证码或邮箱验证码
     * @param userEntity
     * @param businessCode 验证码用途
     * @return
     */
    ResCodeEnum sendCaptchaByUserEntity(CaptchaBusinessEnum businessCode, UserEntity userEntity, String language);

    /**
     * 校验验证码是否正确,当为邮箱验证和手机验证时，receive为邮箱或手机号，当为谷歌验证时receive为userCode，当为行为验证时receive为token
     * @param captchaType 验证码类型
     * @param businessCode 验证码用途
     * @param receive 接收者
     * @param captcha 验证码
     * @param isClearCaptcha 是否清除验证码缓存
     * @return
     */
    ResCodeEnum checkCaptcha(CaptchaTypeEnum captchaType, CaptchaBusinessEnum businessCode,String receive, String captcha, boolean isClearCaptcha) throws IOException;

    /**
     * 根据用户信息，自动判断验证码类型和接收者去校验验证码
     * 不用用于行为验证的校验
     * @param userEntity
     * @param businessCode
     * @param captcha
     * @param isClearCaptcha 是否清除验证码缓存
     * @return
     * @throws IOException
     */
    ResCodeEnum checkCaptcha(UserEntity userEntity, CaptchaBusinessEnum businessCode, String captcha, boolean isClearCaptcha) throws IOException;

    /**
     * 检验验证码校验频率,目前规则定义1min只允许校验10次
     * @param receive
     * @param reason
     * @return
     */
    boolean checkCaptchaLimit(String receive, CaptchaBusinessEnum reason);
}
