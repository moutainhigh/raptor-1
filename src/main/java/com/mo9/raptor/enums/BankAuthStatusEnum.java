package com.mo9.raptor.enums;

/**
 * @author zma
 * @date 2018/9/17
 */
public enum BankAuthStatusEnum {

    SUCCESS("认证成功"),
    FAILED("认证失败"),
    UNAUTH("未认证")
    ;
    private String explanation;
    BankAuthStatusEnum(String explanation){
        this.explanation = explanation;
    }

    public String getExplanation() {
        return explanation;
    }
}
