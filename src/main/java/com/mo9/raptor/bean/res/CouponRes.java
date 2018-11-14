package com.mo9.raptor.bean.res;

import java.math.BigDecimal;

/**
 * Created by xtgu on 2018/11/12.
 * 优惠券
 */
public class CouponRes {
    /**
     * 优惠卷号
     */
    private String  couponsId ;
    /**
     * 状态
     */
    private String  type ;
    /**
     * 失效时间
     */
    private Long  expiryDate ;
    /**
     * 金额
     */
    private String  couponsAmount ;

    /**
     * 限制金额
     */
    private BigDecimal limitAmount ;

    /**
     * 行为
     */
    private String action ;


    public String getCouponsId() {
        return couponsId;
    }

    public void setCouponsId(String couponsId) {
        this.couponsId = couponsId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCouponsAmount() {
        return couponsAmount;
    }

    public void setCouponsAmount(String couponsAmount) {
        this.couponsAmount = couponsAmount;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
