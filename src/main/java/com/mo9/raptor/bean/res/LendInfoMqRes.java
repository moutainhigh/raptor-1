package com.mo9.raptor.bean.res;

import java.math.BigDecimal;

/**
 * 放款mq信息
 * Created by xzhang on 2018/9/20.
 */
public class LendInfoMqRes {

    /** 请求唯一标识 */
    private String loanOrderId;

    /**
     * 用户编号
     */
    private String ownerId;

    /** 请求放款数目 */
    private BigDecimal applyNumber;

    /** 请求放款时间 */
    private Long applyTime;

    /** 姓名 */
    private String userName;

    /** 身份证 */
    private String idCard;

    /** 银行名称 */
    private String bankName;

    /** 银行卡号 */
    private String bankCard;

    /** 银行预留电话 */
    private String bankMobile;

    /**
     * 第三方放款流水号, 比如 mo9
     */
    private String dealCode;

    /** 渠道 */
    private String channel;

    /** 渠道订单ID */
    private String channelOrderId;

    /** 渠道放款数目 */
    private BigDecimal channelLendNumber;

    /** 渠道响应时间 */
    private Long chanelResponseTime;

    public String getLoanOrderId() {
        return loanOrderId;
    }

    public void setLoanOrderId(String loanOrderId) {
        this.loanOrderId = loanOrderId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public BigDecimal getApplyNumber() {
        return applyNumber;
    }

    public void setApplyNumber(BigDecimal applyNumber) {
        this.applyNumber = applyNumber;
    }

    public Long getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Long applyTime) {
        this.applyTime = applyTime;
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

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
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

    public String getDealCode() {
        return dealCode;
    }

    public void setDealCode(String dealCode) {
        this.dealCode = dealCode;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannelOrderId() {
        return channelOrderId;
    }

    public void setChannelOrderId(String channelOrderId) {
        this.channelOrderId = channelOrderId;
    }

    public BigDecimal getChannelLendNumber() {
        return channelLendNumber;
    }

    public void setChannelLendNumber(BigDecimal channelLendNumber) {
        this.channelLendNumber = channelLendNumber;
    }

    public Long getChanelResponseTime() {
        return chanelResponseTime;
    }

    public void setChanelResponseTime(Long chanelResponseTime) {
        this.chanelResponseTime = chanelResponseTime;
    }
}
