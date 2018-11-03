package com.mo9.raptor.enums;

/**
 * Created by xtgu on 2018/11/1.
 * @author xtgu
 * 现金钱包流水类型
 */
public enum BusinessTypeEnum {
    /**
     * 线上还款
     */
    REPAY(),
    /**
     * 代扣
     */
    DEDUCT(),
    /**
     * 入账
     */
    ENTRY(),
    /**
     * 线下还款
     */
    UNDER_LINE(),
    /**
     * 充值
     */
    RECHARGE(),
    /**
     * 退款
     */
    REFUND(),
    ;
}
