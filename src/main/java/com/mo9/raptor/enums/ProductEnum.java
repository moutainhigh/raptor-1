package com.mo9.raptor.enums;

import java.math.BigDecimal;

/**
 * 产品种类
 * TODO: 写数据库
 * Created by xzhang on 2018/9/13.
 */
public enum ProductEnum {

    /**
     * 1000本金, 7天还款
     */
    THOUSAND_SEVEN(new BigDecimal(1000), 7, new BigDecimal(7)),

    ;

    /**
     * 本金
     */
    private BigDecimal principal;

    /**
     * 贷款周期
     */
    private Integer loanDays;

    /**
     * 到期利息
     */
    private BigDecimal interest;

    ProductEnum(BigDecimal principal, Integer loanDays, BigDecimal interest) {
        this.principal = principal;
        this.loanDays = loanDays;
        this.interest = interest;
    }
}
