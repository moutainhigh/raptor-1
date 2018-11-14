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
    ONLINE_REPAY(),
    /**
     * 线上延期
     */
    ONLINE_POSTPONE(),
    /**
     * 代扣
     */
    DEDUCT(),
    /**
     * 线下还款
     */
    UNDERLINE_REPAY(),
    /**
     * 线下延期
     */
    UNDERLINE_POSTPONE(),
    /**
     * 充值
     */
    RECHARGE(),
    /**
     * 退款
     */
    REFUND(),
    /**
     * 线上还款 - 余额
     */
    ONLINE_BALANCE_REPAY(),
    /**
     * 线上延期 - 余额
     */
    ONLINE_BALANCE_POSTPONE(),
    /**
     * 线下还款 - 余额
     */
    UNDERLINE_BALANCE_REPAY(),
    /**
     * 线下延期 - 余额
     */
    UNDERLINE_BALANCE_POSTPONE(),
    ;
}
