package com.mo9.raptor.bean.res;

import java.math.BigDecimal;

/**
 * 还款明细
 * Created by xzhang on 2018/9/20.
 */
public class RepayDetailRes {

    /**
     * 还的类型
     */
    private String fieldType;

    /**
     * 还的金额
     */
    private BigDecimal number;

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public BigDecimal getNumber() {
        return number;
    }

    public void setNumber(BigDecimal number) {
        this.number = number;
    }
}
