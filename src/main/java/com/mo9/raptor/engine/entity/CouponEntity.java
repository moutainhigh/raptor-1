package com.mo9.raptor.engine.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 优惠券 Created by xzhang on 2018/9/28.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_raptor_coupon")
public class CouponEntity extends AbstractStateEntity {

    /**
     * 优惠券号, 唯一编号
     */
    @Column(name = "coupon_id")
    private String couponId;

    /**
     * 用户编号
     */
    @Column(name = "user_code")
    private String userCode;

    /**
     * 优惠券使用类型
     */
    @Column(name = "use_type")
    private String useType;

    /**
     * 绑定订单号
     */
    @Column(name = "bound_order_id")
    private String boundOrderId;

    /**
     * 还款订单号
     */
    @Column(name = "pay_order_id")
    private String payOrderId;

    /**
     * 优惠券使用限定金额 大于此金额才能使用
     */
    @Column(name = "limit_amount")
    private BigDecimal limitAmount = BigDecimal.ZERO;

    /**
     * 优惠券面值
     */
    @Column(name = "apply_amount")
    private BigDecimal applyAmount;

    /**
     * 已入账金额, 绑定订单后有值
     */
    @Column(name = "entry_amount")
    private BigDecimal entryAmount;

    /**
     * 生效起始日期
     */
    @Column(name = "effective_date")
    private Long effectiveDate;

    /**
     * 失效日期
     */
    @Column(name = "expire_date")
    private Long expireDate;

    /**
     * 入账结束时间
     */
    @Column(name = "end_time")
    private Long endTime = -1L;

    @Column(name = "creator")
    private String creator;

    @Column(name = "reason")
    private String reason;


    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getBoundOrderId() {
        return boundOrderId;
    }

    public void setBoundOrderId(String boundOrderId) {
        this.boundOrderId = boundOrderId;
    }

    public String getPayOrderId() {
        return payOrderId;
    }

    public void setPayOrderId(String payOrderId) {
        this.payOrderId = payOrderId;
    }

    public BigDecimal getApplyAmount() {
        return applyAmount;
    }

    public void setApplyAmount(BigDecimal applyAmount) {
        this.applyAmount = applyAmount;
    }

    public BigDecimal getEntryAmount() {
        return entryAmount;
    }

    public void setEntryAmount(BigDecimal entryAmount) {
        this.entryAmount = entryAmount;
    }

    public Long getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Long effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Long expireDate) {
        this.expireDate = expireDate;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUseType() {
        return useType;
    }

    public void setUseType(String useType) {
        this.useType = useType;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }
}
