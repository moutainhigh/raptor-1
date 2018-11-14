package com.mo9.raptor.bean.req;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by xzhang on 2018/10/10.
 */
public class OfflineRepayReq {

    /**
     * 用户唯一编号
     */
    @NotBlank
    private String userCode;

    /**
     * 订单号
     */
    @NotBlank
    private String orderId;

    /**
     * 还款类型
     */
    @NotBlank
    private String type;

    /**
     * 还款金额
     */
    @NotNull
    @Min(0)
    private BigDecimal amount;

    /**
     * 签名
     */
    @NotBlank
    private String sign;

    /**
     * 创建者
     */
    @NotBlank
    private String creator;

    /**
     * 减免原因
     */
    private String reliefReason;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getReliefReason() {
        return reliefReason;
    }

    public void setReliefReason(String reliefReason) {
        this.reliefReason = reliefReason;
    }
}
