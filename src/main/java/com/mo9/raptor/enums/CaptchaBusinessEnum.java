package com.mo9.libracredit.enums;


import com.mo9.libracredit.redis.RedisParams;

/**
 * 须发送验证码业务枚举
 * @author zma
 * @date 2018/7/5
 */

public enum CaptchaBusinessEnum {

    REGISTER("REGISTER", "注册", RedisParams.EXPIRE_30M),
    LOGIN("LOGIN", "登录", RedisParams.EXPIRE_30M),
//    SET_DEAL_PASSWORD("SET_DEAL_PASSWORD", "设置交易密码", RedisParams.EXPIRE_30M),
    MODIFY_DEAL_PASSWORD("MODIFY_DEAL_PASSWORD", "修改交易密码", RedisParams.EXPIRE_30M),
    RESET_DEAL_PASSWORD("RESET_DEAL_PASSWORD", "重置交易密码", RedisParams.EXPIRE_30M),
    RESET_PASSWORD("RESET_PASSWORD", "重置登录密码", RedisParams.EXPIRE_5M),
    MODIFY_PASSWORD("MODIFY_PASSWORD", "修改登录密码", RedisParams.EXPIRE_30M),
    BINDING_EMAIL("BINDING_EMAIL","绑定邮箱",RedisParams.EXPIRE_30M),
    BINDING_MOBILE("BINDING_MOBILE","绑定手机",RedisParams.EXPIRE_30M),
    BINDING_GOOGLE("BINDING_GOOGLE","绑定谷歌",RedisParams.EXPIRE_30M),
    MODIFY_BINDING_MOBILE("MODIFY_MOBILE","修改绑定手机",RedisParams.EXPIRE_30M),
    MODIFY_BINDING_EMAIL("MODIFY_EMAIL","修改绑定邮箱",RedisParams.EXPIRE_30M),
    CLOSE_BINDING_EMAIL("CLOSE_EMAIL","关闭邮箱验证",RedisParams.EXPIRE_30M),
    CLOSE_BINDING_MOBILE("CLOSE_MOBILE","关闭手机验证",RedisParams.EXPIRE_30M),
    CLOSE_BINDING_GOOGLE("CLOSE_GOOGLE", "关闭谷歌验证", RedisParams.EXPIRE_30M),
    PUT_FORWARD("PUT_FORWARD","提现",RedisParams.EXPIRE_30M),
    UNLOGIN_SEND_CAPTCHA("UNLOGIN_SEND_CAPTCHA", "未登录发送验证码", RedisParams.EXPIRE_30M),
    ;

    String type;

    String desc;

    /**
     * 验证码有效期
     */
    Long expireTime;

    public String getDesc() {
        return desc;
    }

    public String getType() {
        return type;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    CaptchaBusinessEnum(String type, String desc, Long expireTime) {
        this.type = type;
        this.desc = desc;
        this.expireTime = expireTime;
    }
}
