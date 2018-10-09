package com.mo9.raptor.enums;

/**
 * 消息发送模板枚举
 * @author zma
 * @date 2018/7/11
 */
public enum MessageNotifyModelEnum {

    GENERAL_CAPTCHA("通用验证码"),

    Raptor_sms_loginCaptcha_CN("登录短信验证码");

    private String name ;

    MessageNotifyModelEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
