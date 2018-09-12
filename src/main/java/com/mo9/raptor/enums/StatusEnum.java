package com.mo9.raptor.enums;

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

    /**
     * 放款之前的有效订单
     */
    public static final List<StatusEnum> BEFORE_LENDING = Arrays.asList(PENDING, AUDITING, PASSED, DEDUCTING);

    /**
     * 入账失败状态
     */
    public static final List<StatusEnum> COULD_ENTRY_AGAIN = Arrays.asList(DEDUCTED, ENTRY_DOING);
    public static final List<String> COULD_ENTRY_AGAIN_NAME = Arrays.asList(DEDUCTED.name(), ENTRY_DOING.name());



    /**
     * 失败订单
     */
    public static final List<StatusEnum> FAILED_LIST = Arrays.asList(REJECTED, FAILED, DEDUCT_FAILED, ENTRY_FAILED);

    /**
     * 订单进行中的状态
     */
    public static final List<String> PROCESSING = Arrays.asList(PENDING.name(), PASSED.name(), AUDITING.name(), LENDING.name(), LENT.name());

    /**
     * 正式下单后的状态
     */
    public static final List<String> AFTER_PRE_ORDER_PROCESSING = Arrays.asList(PASSED.name(), AUDITING.name(), LENDING.name(), LENT.name());

    /**
     * 放款中订单
     */
    public static final List<String> PENDING_LIST = Arrays.asList(PENDING.name(), PASSED.name(), AUDITING.name(), LENDING.name());

    /**
     * 已放款, 待还款状态
     */
    public static final List<String> LENT_LIST = Arrays.asList(LENT.name());

    /**
     * 超时
     */
    public static final List<StatusEnum> EXPIRED_LIST = Arrays.asList(EXPIRED);

    /**
     * 地址生成费绑定状态
     */
    public static final List<String> ADDRESS_FEE_BIND = Arrays.asList(LENT.name(), PAYOFF.name(),  LIQUIDATED_BROKE.name());


    StatusEnum(String explanation) {
        this.explanation = explanation;
    }

    public String getExplanation() {
        return explanation;
    }

}
