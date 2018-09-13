package com.mo9.raptor.enums;


import com.mo9.raptor.redis.RedisParams;

/**
 * 须发送验证码业务枚举
 * @author zma
 * @date 2018/7/5
 */

public enum CaptchaBusinessEnum {

    LOGIN("LOGIN", "登录", RedisParams.EXPIRE_30M),
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
