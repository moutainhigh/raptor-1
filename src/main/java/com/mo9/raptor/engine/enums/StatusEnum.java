package com.mo9.raptor.engine.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Created by gqwu on 2018/3/23.
 */
public enum StatusEnum {

    /** 通用状态 */
    PENDING("排队中"),
    AUDITING("审核中"),
    PASSED("审核通过"),
    REJECTED("已拒绝"),
    EXPIRED("已超时"),
    CANCELLED("已取消"),

    /** 贷款订单状态 */
    LENDING("放款中"),
    LENT("已放款"),
    FAILED("放款失败"),
    PAYOFF("已还清"),
    LIQUIDATED_BROKE("已破产"),

    /** 还款订单状态 */
    DEDUCTING("扣款中"),
    DEDUCTED("已扣款"),
    DEDUCT_FAILED("扣款失败"),
    ENTRY_DOING("入账中"),
    ENTRY_DONE("已入账"),
    ENTRY_FAILED("入账失败"),
    REFUNDED("已退款"),


    /** 策略状态 */
    EXECUTING("执行中"),
    BUNDLED("已绑定"),

    ;

    private String explanation;


    StatusEnum(String explanation) {
        this.explanation = explanation;
    }

    public String getExplanation() {
        return explanation;
    }

    /**
     * 放款之前的订单
     */
    public static final List<StatusEnum> BEFORE_LENDING = Arrays.asList(PENDING, AUDITING, PASSED, DEDUCTING, EXPIRED, CANCELLED, LENDING, FAILED);

}
