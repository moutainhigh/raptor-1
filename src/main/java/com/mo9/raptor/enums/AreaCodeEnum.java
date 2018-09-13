package com.mo9.raptor.enums;


import java.util.Locale;

/**
 * 地区相关枚举
 *
 * @author zma
 * @date 2018/7/13
 */
public enum AreaCodeEnum {
    //大陆地区暂时仅支持中文简体
    CN("大陆地区", Locale.SIMPLIFIED_CHINESE, "0086")

    ;
    /**
     * 描述
     */
    private String description;
    /**
     * 语言代码
     */
    private Locale languageCode;
    /**
     * 国际区号
     */
    private String phoneCode;

    AreaCodeEnum(String description, Locale languageCode, String phoneCode) {
        this.description = description;
        this.languageCode = languageCode;
        this.phoneCode = phoneCode;
    }

    public String getDescription() {
        return description;
    }

    public Locale getLanguageCode() {
        return languageCode;
    }

    public String getPhoneCode() {
        return phoneCode;
    }
}
