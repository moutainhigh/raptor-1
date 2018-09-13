package com.mo9.raptor.enums;


/**
 * 消息发送情形枚举
 * @author zma
 * @date 2018/7/11
 */
public enum MessageNotifyEventEnum {

    GENERAL_CAPTCHA("通用验证码"),
    loginCaptcha("登录验证码")
    ;

    private String explanation ;


    MessageNotifyEventEnum(String explanation) {
        this.explanation = explanation;
    }

    public String getExplanation() {
        return explanation;
    }
}
