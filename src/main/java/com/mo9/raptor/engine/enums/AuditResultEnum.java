package com.mo9.raptor.engine.enums;

/**
 * Created by jyou on 2018/10/9.
 *
 * @author jyou
 */
public enum  AuditResultEnum {
    PASS("通过"),
    REJECTED("拒绝"),
    MANUAL("人工");

    private String desc;

    AuditResultEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
