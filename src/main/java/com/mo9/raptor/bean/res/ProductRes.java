package com.mo9.raptor.bean.res;

import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by xtgu on 2018/9/16.
 * @author xtgu
 * 查询产品信息
 */
public class ProductRes {

    /**
     * 借款金额
     */
    private BigDecimal loanable;

    /**
     * 借款期限
     */
    private Integer period;

    /**
     * 利息
     */
    private BigDecimal interest;

    /**
     * 实际到账
     */
    private BigDecimal actuallyGet;

    public BigDecimal getLoanable() {
        return loanable;
    }

    public void setLoanable(BigDecimal loanable) {
        this.loanable = loanable;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public BigDecimal getActuallyGet() {
        return actuallyGet;
    }

    public void setActuallyGet(BigDecimal actuallyGet) {
        this.actuallyGet = actuallyGet;
    }
}
