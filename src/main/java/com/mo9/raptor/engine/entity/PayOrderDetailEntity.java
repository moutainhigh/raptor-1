package com.mo9.raptor.engine.entity;

import com.mo9.raptor.entity.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 还款明细
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_raptor_pay_order_detail")
public class PayOrderDetailEntity extends BaseEntity {

    /**
     * 用户
     */
    @Column(name = "owner_id")
    private String ownerId;

    /**
     * 销帐类型  BillDestinationTypeEnum
     */
    @Column(name = "dest_type")
    private String destType;

    /**
     * 还的借款订单
     */
    @Column(name = "loan_order_id")
    private String loanOrderId;

    /**
     * 入账源类型  BillSourceTypeEnum
     */
    @Column(name = "source_type")
    private String sourceType;

    /**
     * 还款订单
     */
    @Column(name = "pay_order_id")
    private String payOrderId;

    /**
     * 支付币种
     */
    @Column(name = "pay_currency")
    private String payCurrency;

    /**ItemTypeEnum
     * 期类型, 当期, 往期, 未出
     */
    @Column(name = "item_type")
    private String itemType;

    /**
     * 还款日
     */
    @Column(name = "repay_day")
    private Long repayDay;

    /**
     * 所还账单类型  FieldEnum
     */
    @Column(name = "field")
    private String field;

    /**
     * 应付当期本金
     */
    @Column(name = "should_pay")
    private BigDecimal shouldPay = BigDecimal.ZERO;

    /**
     * 本次还款所还本金
     */
    @Column(name = "paid")
    private BigDecimal paid = BigDecimal.ZERO;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getLoanOrderId() {
        return loanOrderId;
    }

    public void setLoanOrderId(String loanOrderId) {
        this.loanOrderId = loanOrderId;
    }

    public String getPayOrderId() {
        return payOrderId;
    }

    public void setPayOrderId(String payOrderId) {
        this.payOrderId = payOrderId;
    }

    public String getPayCurrency() {
        return payCurrency;
    }

    public void setPayCurrency(String payCurrency) {
        this.payCurrency = payCurrency;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Long getRepayDay() {
        return repayDay;
    }

    public void setRepayDay(Long repayDay) {
        this.repayDay = repayDay;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public BigDecimal getShouldPay() {
        return shouldPay;
    }

    public void setShouldPay(BigDecimal shouldPay) {
        this.shouldPay = shouldPay;
    }

    public BigDecimal getPaid() {
        return paid;
    }

    public void setPaid(BigDecimal paid) {
        this.paid = paid;
    }

    public String getDestType() {
        return destType;
    }

    public void setDestType(String destType) {
        this.destType = destType;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public void create() {
        this.setCreateTime(System.currentTimeMillis());
        this.setUpdateTime(System.currentTimeMillis());
    }
}
