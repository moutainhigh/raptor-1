package com.mo9.raptor.engine.structure.field;

/**
 * 入账源类型
 * Created by xzhang on 2018/7/21.
 */
public enum SourceTypeEnum {

    /**
     * 还款订单
     */
    PAY_ORDER(),

    /**
     * 优惠
     */
    COUPON(),

    /**
     * 现金钱包剩余金额还款
     */
    CASH_REPAY(),
    ;
}
