package com.mo9.raptor.bean.req;


import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 还款请求
 * Created by xzhang on 2018/9/13.
 */
public class OrderAddReq {

    /**
     * 本金
     */
    @NotNull
    private BigDecimal capital;

    /**
     * 周期
     */
    private int period;

    public BigDecimal getCapital() {
        return capital;
    }

    public void setCapital(BigDecimal capital) {
        this.capital = capital;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}
