package com.mo9.raptor.bean.res;

import java.math.BigDecimal;

/**
 * 还款mq通知
 * Created by xzhang on 2018/9/20.
 */
public class RepayInfoMqRes {

    /**
     * 还的借款订单号
     */
    private String orderId;

    /**
     * 用户编号
     */
    private String userCode;

    /**
     * 还款订单号
     */
    private String payOrderId;

    /**
     * 还款申请金额
     */
    private BigDecimal repayAmount;

    /**
     * 渠道返回的还款数目
     */
    private BigDecimal channelRepayNumber;

    /**
     * 还款渠道
     */
    private String channel;

    /**
     * 客户端Id
     */
    private String clientId;

    /**
     * 客户端版本号
     */
    private String clientVersion;

    /**
     * 客户端版本号
     */
    private String bankCard;

    /**
     * 客户端版本号
     */
    private String bankMobile;

    /**
     * 客户端版本号
     */
    private String userName;

    /**
     * 客户端版本号
     */
    private String idCard;

    /**
     * 第三方放款流水号, 比如 mo9
     */
    private String dealCode;

    /**
     * 银行放款流水号
     */
    private String thirdChannelNo;

    /**
     * 延期天数
     */
    private Integer postponeDays;

    /**
     * 是否入账完成
     */
    private Boolean isEntryDone;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getPayOrderId() {
        return payOrderId;
    }

    public void setPayOrderId(String payOrderId) {
        this.payOrderId = payOrderId;
    }

    public BigDecimal getRepayAmount() {
        return repayAmount;
    }

    public void setRepayAmount(BigDecimal repayAmount) {
        this.repayAmount = repayAmount;
    }

    public BigDecimal getChannelRepayNumber() {
        return channelRepayNumber;
    }

    public void setChannelRepayNumber(BigDecimal channelRepayNumber) {
        this.channelRepayNumber = channelRepayNumber;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
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

    public String getDealCode() {
        return dealCode;
    }

    public void setDealCode(String dealCode) {
        this.dealCode = dealCode;
    }

    public String getThirdChannelNo() {
        return thirdChannelNo;
    }

    public void setThirdChannelNo(String thirdChannelNo) {
        this.thirdChannelNo = thirdChannelNo;
    }

    public Integer getPostponeDays() {
        return postponeDays;
    }

    public void setPostponeDays(Integer postponeDays) {
        this.postponeDays = postponeDays;
    }

    public Boolean getEntryDone() {
        return isEntryDone;
    }

    public void setEntryDone(Boolean entryDone) {
        isEntryDone = entryDone;
    }
}
