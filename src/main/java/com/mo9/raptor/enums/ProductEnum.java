package com.mo9.raptor.enums;

import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.List;

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
    /**
     * 1000本金, 14天还款
     */
    THOUSAND_FOURTEEN(new BigDecimal(1000), 14, new BigDecimal(14)),

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

    public static Boolean checkPrincipal (BigDecimal principal) {
        for (ProductEnum productEnum : ProductEnum.values()) {
            if (productEnum.principal.compareTo(principal) == 0) {
                return true;
            }
        }
        return false;
    }

    public static Boolean checkLoanDays (Integer loanDays) {
        for (ProductEnum productEnum : ProductEnum.values()) {
            if (productEnum.loanDays.equals(loanDays)) {
                return true;
            }
        }
        return false;
    }
}
