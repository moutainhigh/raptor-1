package com.mo9.raptor.engine.enums;

/**
 * Created by gqwu on 2018/3/23.
 * 审核类型
 */
public enum AuditModeEnum {
    AUTO("自动审核"),
    MANUAL("人工审核"),
    ;

    private String explanation;

    AuditModeEnum(String explanation){
        this.explanation = explanation;
    }

    public String getExplanation() {
        return explanation;
    }

}
