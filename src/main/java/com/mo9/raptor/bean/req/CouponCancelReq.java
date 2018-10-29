package com.mo9.raptor.bean.req;


import javax.validation.constraints.NotBlank;

/**
 * 优惠券取消请求
 * Created by xzhang on 2018/10/29.
 */
public class CouponCancelReq {

    /** 绑定借贷订单ID */
    @NotBlank
    private String bundleId;

    /** 操作者 */
    @NotBlank
    private String operator;

    /** 签名 */
    @NotBlank
    private String sign;

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
