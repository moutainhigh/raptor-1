package com.mo9.raptor.bean.req;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 优惠券更新请求
 * Created by gqwu on 2018/9/29.
 */
public class CouponUpdateReq {

    /**
     * 修改后的优惠金额
     */
    @NotNull
    @Min(0)
    private BigDecimal number;

    /**
     * 绑定借贷订单ID, 空为解绑
     */
    private String bundleId;

    /**
     * 修改者
     */
    @NotBlank
    private String creator;

    /**
     * 修改原因
     */
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
