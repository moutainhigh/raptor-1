package com.mo9.raptor.bean.req;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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

    public String getLoanOrderId() {
        return loanOrderId;
    }

    public void setLoanOrderId(String loanOrderId) {
        this.loanOrderId = loanOrderId;
    }
}
