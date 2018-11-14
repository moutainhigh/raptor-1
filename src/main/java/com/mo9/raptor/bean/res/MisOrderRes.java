package com.mo9.raptor.bean.res;

import com.mo9.raptor.engine.enums.NumberMode;

import java.math.BigDecimal;

/**
 * mis需要的订单信息
 * Created by xzhang on 2018/9/19.
 */
public class MisOrderRes {

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 用户
     */
    private String ownerId;

    /**
     * 类型
     */
    private String productType;

    /**
     * 状态
     */
    private String status;

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

    /** 优惠 */
    private BigDecimal reliefAmount;

    /** 延期单位服务费 */
    private BigDecimal postponeUnitCharge;

    /** 延期单位服务费 */
    private Integer postponeCount;

    /** 放款时间 */
    private Long lendTime;

    /** 还清时间 */
    private Long payoffTime;

    /** 还款日 */
    private Long repaymentDate;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public BigDecimal getReliefAmount() {
        return reliefAmount;
    }

    public void setReliefAmount(BigDecimal reliefAmount) {
        this.reliefAmount = reliefAmount;
    }

    public BigDecimal getPostponeUnitCharge() {
        return postponeUnitCharge;
    }

    public void setPostponeUnitCharge(BigDecimal postponeUnitCharge) {
        this.postponeUnitCharge = postponeUnitCharge;
    }

    public Long getLendTime() {
        return lendTime;
    }

    public void setLendTime(Long lendTime) {
        this.lendTime = lendTime;
    }

    public Long getPayoffTime() {
        return payoffTime;
    }

    public void setPayoffTime(Long payoffTime) {
        this.payoffTime = payoffTime;
    }

    public Long getRepaymentDate() {
        return repaymentDate;
    }

    public void setRepaymentDate(Long repaymentDate) {
        this.repaymentDate = repaymentDate;
    }

    public Integer getPostponeCount() {
        return postponeCount;
    }

    public void setPostponeCount(Integer postponeCount) {
        this.postponeCount = postponeCount;
    }
}
