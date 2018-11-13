package com.mo9.raptor.bean.req;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * 还款请求
 * Created by xzhang on 2018/9/13.
 */
public class LoanOrderRepay {

    /**
     * 还款订单号
     */
    @NotBlank
    private String loanOrderId;
    /**
     * 优惠卷号
     */
    private String couponId;
    /**
     * 钱包余额
     */
    private BigDecimal balance;

    public String getLoanOrderId() {
        return loanOrderId;
    }

    public void setLoanOrderId(String loanOrderId) {
        this.loanOrderId = loanOrderId;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
