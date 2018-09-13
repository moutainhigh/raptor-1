package com.mo9.raptor.enums;

/**
 * Created by xzhang on 2018/9/13.
 */
public enum ChannelUseType {

    /**
     * 连接
     */
    LINK("link"),

    /**
     * 直接是html页面
     */
    HTML("html"),

    /**
     * 需调用sdk
     */
    SDK("sdk"),

    ;

    private String desc;

    public String getDesc() {
        return desc;
    }

    ChannelUseType(String desc) {
        this.desc = desc;
    }
}
