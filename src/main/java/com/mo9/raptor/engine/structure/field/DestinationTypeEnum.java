package com.mo9.raptor.engine.structure.field;

/**
 * 入账目标类型
 * Created by xzhang on 2018/7/21.
 */
public enum DestinationTypeEnum {

    /**
     * 借款订单
     */
    LOAN_ORDER(),

    /**
     * 地址生成费
     */
    ADDRESS_FEE(),

    /**
     * 还款策略
     */
    PAY_STRATEGY(),

    ;
}
