package com.mo9.raptor.engine.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_raptor_pay_order")
public class PayOrderEntity extends AbstractOrderEntity {

    @Column(name = "batch_id")
    private String batchId;

    /** 支付币种 */
    @Column(name = "pay_currency")
    private String payCurrency;

    /** 请求支付数量 */
    @Column(name = "apply_number")
    private BigDecimal applyNumber;

    /** 实际支付数量 */
    @Column(name = "pay_number")
    private BigDecimal payNumber = BigDecimal.ZERO;

    /** 确认时间 */
    @Column(name = "confirm_time")
    private Long confirmTime = -1L;

    /** 锚定币种 */
    @Column(name = "anchor_currency")
    private String anchorCurrency;

    /** 兑换率 */
    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;

    /** 按锚定币种换算后数量 */
    @Column(name = "anchor_number")
    private BigDecimal anchorNumber;

    /** 实际入账数量（按锚定币种计数） */
    @Column(name = "entry_number")
    private BigDecimal entryNumber = BigDecimal.ZERO;

    /** 支付时间 */
    @Column(name = "pay_time")
    private long payTime = -1L;

    /** 入账时间 */
    @Column(name = "entry_over_time")
    private long entryOverTime = -1L;

    /** 还款订单 */
    @Column(name = "loan_order_id")
    private String loanOrderId;

    public String getPayCurrency() {
        return payCurrency;
    }

    public void setPayCurrency(String payCurrency) {
        this.payCurrency = payCurrency;
    }

    public BigDecimal getApplyNumber() {
        return applyNumber;
    }

    public void setApplyNumber(BigDecimal applyNumber) {
        this.applyNumber = applyNumber;
    }

    public BigDecimal getPayNumber() {
        return payNumber;
    }

    public void setPayNumber(BigDecimal payNumber) {
        this.payNumber = payNumber;
    }

    public Long getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Long confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getAnchorCurrency() {
        return anchorCurrency;
    }

    public void setAnchorCurrency(String anchorCurrency) {
        this.anchorCurrency = anchorCurrency;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getAnchorNumber() {
        return anchorNumber;
    }

    public void setAnchorNumber(BigDecimal anchorNumber) {
        this.anchorNumber = anchorNumber;
    }

    public BigDecimal getEntryNumber() {
        return entryNumber;
    }

    public void setEntryNumber(BigDecimal entryNumber) {
        this.entryNumber = entryNumber;
    }

    public long getPayTime() {
        return payTime;
    }

    public void setPayTime(long payTime) {
        this.payTime = payTime;
    }

    public long getEntryOverTime() {
        return entryOverTime;
    }

    public void setEntryOverTime(long entryOverTime) {
        this.entryOverTime = entryOverTime;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getLoanOrderId() {
        return loanOrderId;
    }

    public void setLoanOrderId(String loanOrderId) {
        this.loanOrderId = loanOrderId;
    }
}
