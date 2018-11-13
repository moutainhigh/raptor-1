package com.mo9.raptor.bean.req;

import java.io.Serializable;
import java.math.BigDecimal;

public class PayInfoCache implements Serializable {

    private String userCode;

    private String userName;

    private String idCard;

    private String loanOrderId;

    private String payType;

    private BigDecimal payNumber;

    private int period;

    private String clientId;

    private String clientVersion;

    /**
     * 优惠卷号
     */
    private String couponId;
    /**
     * 钱包余额
     */
    private BigDecimal balance;

    public String getLoanOrderId() {
        return loanOrderId;
    }

    public void setLoanOrderId(String loanOrderId) {
        this.loanOrderId = loanOrderId;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public BigDecimal getPayNumber() {
        return payNumber;
    }

    public void setPayNumber(BigDecimal payNumber) {
        this.payNumber = payNumber;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
