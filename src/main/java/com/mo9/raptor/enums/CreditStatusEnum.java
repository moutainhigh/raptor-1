package com.mo9.raptor.enums;

/**
 * 用户的信用状态
 * @author zma
 * @date 2018/10/8
 */
public enum  CreditStatusEnum {

    INITIAL("用户初始状态"),
    /**
     * 延期，提前，按期还款均为正常还款行为
     */
    REPAY_REGULAR("正常还款"),

    REPAY_OVERDUE("存在逾期还款")
    ;

    private String explanation;

    public String getExplanation() {
        return explanation;
    }

    CreditStatusEnum(String explanation) {
        this.explanation = explanation;
    }
}
