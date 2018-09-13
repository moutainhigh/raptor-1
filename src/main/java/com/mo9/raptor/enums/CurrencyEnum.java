package com.mo9.raptor.enums;

/**
 * Created by xzhang on 2018/9/13.
 */
public enum CurrencyEnum {

    /**
     * 人民币
     */
    CNY(),


    ;


    /**
     * 获得对当前默认币种
     * @return
     */
    public static CurrencyEnum getDefaultCurrency() {
        return CNY;
    }

}
