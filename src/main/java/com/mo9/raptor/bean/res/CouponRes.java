package com.mo9.raptor.bean.res;

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
}
