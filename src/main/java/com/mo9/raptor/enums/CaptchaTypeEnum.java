package com.mo9.libracredit.enums;

/**
 * 验证码类型
 * @author zma
 * @date 2018/7/5
 */
public enum CaptchaTypeEnum {
    MOBILE("MOBILE", "手机号验证码"),
    EMAIL("EMAIL", "邮箱验证码"),
    GOOGLE("GOOGLE", "谷歌验证码"),
    BEHAVIOR("BEHAVIOR", "行为验证"),
    ;

    String type;

    String desc;

    public String getDesc() {
        return desc;
    }

    public String getType() {
        return type;
    }

    CaptchaTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
