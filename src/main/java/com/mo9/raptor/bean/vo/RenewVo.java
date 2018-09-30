package com.mo9.raptor.bean.vo;

import java.math.BigDecimal;

/**
 * 延期明细
 * Created by xzhang on 2018/9/30.
 */
public class RenewVo {

    /**
     * 延期天数
     */
    private Integer period;

    /**
     * 需要金额
     */
    private BigDecimal amount;

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
