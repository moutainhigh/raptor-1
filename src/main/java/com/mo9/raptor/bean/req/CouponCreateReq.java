package com.mo9.raptor.bean.req;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 优惠券创建请求
 * Created by gqwu on 2018/9/29.
 */
public class CouponCreateReq {

    /** 优惠金额 */
    @NotNull
    private BigDecimal number;

    /** 绑定借贷订单ID */
    @NotBlank
    private String bundleId;

    /** 创建者 */
    @NotBlank
    private String creator;

    /** 优惠原因 */
    @NotBlank
    private String reason;

    /** 签名 */
    @NotBlank
    private String sign;

    public BigDecimal getNumber() {
        return number;
    }

    public void setNumber(BigDecimal number) {
        this.number = number;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
