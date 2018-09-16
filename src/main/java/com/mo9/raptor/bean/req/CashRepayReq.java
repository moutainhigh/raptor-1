package com.mo9.raptor.bean.req;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 还款请求
 * Created by xzhang on 2018/9/13.
 */
public class CashRepayReq {

    /**
     * 付款渠道类型
     */
    @NotNull
    private Integer channelType;

    /**
     * 银行卡号
     */
    @NotBlank
    private String bankCard;

    /**
     * 预留手机号
     */
    @NotBlank
    @Pattern(regexp = "^1[0-9]{10}$", message = "手机号不符合规则")
    private String bankMobile;

    /**
     * 真实姓名
     */
    @NotBlank
    private String userName;

    /**
     * 身份证号
     */
    @NotBlank
    @Pattern(regexp = "^\\d{14,19}[0-9Xx]$", message = "身份证号不符合规则")
    private String idCard;

    /**
     * 订单号
     */
    @NotBlank
    private String orderId;

    public Integer getChannelType() {
        return channelType;
    }

    public void setChannelType(Integer channelType) {
        this.channelType = channelType;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getBankMobile() {
        return bankMobile;
    }

    public void setBankMobile(String bankMobile) {
        this.bankMobile = bankMobile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
