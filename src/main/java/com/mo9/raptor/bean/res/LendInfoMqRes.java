package com.mo9.raptor.bean.res;

import com.mo9.raptor.engine.enums.NumberMode;

import javax.persistence.Column;
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

    private String orderType;

    private String orderStatus;

    private BigDecimal reliefAmount = BigDecimal.ZERO;

    private Integer postponeCount;

    private Long repaymentTime;

    /** 借贷数目 */
    private BigDecimal loanNumber;

    /** 借贷期限 */
    private Integer loanTerm;

    /** 放款数目 */
    private BigDecimal lentNumber;

    /** 利息模式 - 默认固定利息 */
    private String interestMode = NumberMode.QUANTITY.name();

    /** 利息模式下对应值 */
    private BigDecimal interestValue;

    /** 罚息模式 */
    private String penaltyMode = NumberMode.QUANTITY.name();

    /** 利息模式下对应值 */
    private BigDecimal penaltyValue;

    /** 借贷服务费 */
    private BigDecimal chargeValue;

    /** 延期单位服务费 */
    private BigDecimal postponeUnitCharge;

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
     * mo9放款流水号,
     */
    private String dealCode;

    /** 渠道 */
    private String channel;

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

    public BigDecimal getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(BigDecimal loanNumber) {
        this.loanNumber = loanNumber;
    }

    public Integer getLoanTerm() {
        return loanTerm;
    }

    public void setLoanTerm(Integer loanTerm) {
        this.loanTerm = loanTerm;
    }

    public BigDecimal getLentNumber() {
        return lentNumber;
    }

    public void setLentNumber(BigDecimal lentNumber) {
        this.lentNumber = lentNumber;
    }

    public String getInterestMode() {
        return interestMode;
    }

    public void setInterestMode(String interestMode) {
        this.interestMode = interestMode;
    }

    public BigDecimal getInterestValue() {
        return interestValue;
    }

    public void setInterestValue(BigDecimal interestValue) {
        this.interestValue = interestValue;
    }

    public String getPenaltyMode() {
        return penaltyMode;
    }

    public void setPenaltyMode(String penaltyMode) {
        this.penaltyMode = penaltyMode;
    }

    public BigDecimal getPenaltyValue() {
        return penaltyValue;
    }

    public void setPenaltyValue(BigDecimal penaltyValue) {
        this.penaltyValue = penaltyValue;
    }

    public BigDecimal getChargeValue() {
        return chargeValue;
    }

    public void setChargeValue(BigDecimal chargeValue) {
        this.chargeValue = chargeValue;
    }

    public BigDecimal getPostponeUnitCharge() {
        return postponeUnitCharge;
    }

    public void setPostponeUnitCharge(BigDecimal postponeUnitCharge) {
        this.postponeUnitCharge = postponeUnitCharge;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getReliefAmount() {
        return reliefAmount;
    }

    public void setReliefAmount(BigDecimal reliefAmount) {
        this.reliefAmount = reliefAmount;
    }

    public Integer getPostponeCount() {
        return postponeCount;
    }

    public void setPostponeCount(Integer postponeCount) {
        this.postponeCount = postponeCount;
    }

    public Long getRepaymentTime() {
        return repaymentTime;
    }

    public void setRepaymentTime(Long repaymentTime) {
        this.repaymentTime = repaymentTime;
    }
}
