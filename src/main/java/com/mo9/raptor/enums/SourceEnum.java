package com.mo9.raptor.enums;

/**
 * Created by jyou on 2018/9/28.
 *
 * @author jyou
 */
public enum SourceEnum {
    WHITE("白名单用户"),
    NEW("新用户");

    private String desc;

    SourceEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
