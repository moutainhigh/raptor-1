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

    public boolean verify () {
        return true;
    }

    /**
     * 支付币种
     */
    @Column(name = "pay_currency")
    private String payCurrency;

    /**
     * 还的借款订单
     */
    @Column(name = "loan_order_id")
    private String loanOrderId;

    /**
     * 还款渠道
     */
    @Column(name = "channel")
    private String channel;

    /**
     * 请求支付数量
     */
    @Column(name = "apply_number")
    private BigDecimal applyNumber;

    /**
     * 实际支付数量
     */
    @Column(name = "pay_number")
    private BigDecimal payNumber = BigDecimal.ZERO;

    /**
     * 实际入账数量
     */
    @Column(name = "entry_number")
    private BigDecimal entryNumber = BigDecimal.ZERO;

    /**
     * 支付时间
     */
    @Column(name = "pay_time")
    private long payTime = -1L;

    /**
     * 入账时间
     */
    @Column(name = "entry_over_time")
    private long entryOverTime = -1L;

    /**
     * 还除本金之外的所有金额时, 可推迟还款时间, 一般为一个账期
     * TODO: 可能删掉
     */
    @Column(name = "postpone_days")
    private Integer postponeDays = 0;

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

    public String getLoanOrderId() {
        return loanOrderId;
    }

    public void setLoanOrderId(String loanOrderId) {
        this.loanOrderId = loanOrderId;
    }

    public Integer getPostponeDays() {
        return postponeDays;
    }

    public void setPostponeDays(Integer postponeDays) {
        this.postponeDays = postponeDays;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void create() {
        this.setCreateTime(System.currentTimeMillis());
        this.setUpdateTime(System.currentTimeMillis());
    }
}
