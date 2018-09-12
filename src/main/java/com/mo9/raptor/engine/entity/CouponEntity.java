package com.mo9.raptor.engine.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 优惠券 Created by gqwu on 2018/7/6.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_raptor_coupon")
public class CouponEntity extends AbstractStrategyEntity implements IStateEntity {

    /** 优惠券所有者 */
    @Column(name = "owner_id")
    private String ownerId;

    /** 优惠限定范围-用户、订单（补充：业务粒度 的优惠约束，由策略实现，分期款项粒度 的优惠由十字交叉约束（itemType X fieldType）实现） */
    @Column(name = "scope_bundle")
    private String scopeBundle;

    /** 优惠限定范围内关联对象ID */
    @Column(name = "scope_bundle_id")
    private String scopeBundleId;

    /** 优惠券结算方式 - 一次性结算、持续结算 */
    @Column(name = "entry_mode")
    private String entryMode;

    /** 已入账优惠金额 */
    @Column(name = "entry_number")
    private BigDecimal entryNumber;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getScopeBundleId() {
        return scopeBundleId;
    }

    public void setScopeBundleId(String scopeBundleId) {
        this.scopeBundleId = scopeBundleId;
    }

    public String getEntryMode() {
        return entryMode;
    }

    public void setEntryMode(String entryMode) {
        this.entryMode = entryMode;
    }

    public String getScopeBundle() {
        return scopeBundle;
    }

    public void setScopeBundle(String scopeBundle) {
        this.scopeBundle = scopeBundle;
    }

    public BigDecimal getEntryNumber() {
        return entryNumber;
    }

    public void setEntryNumber(BigDecimal entryNumber) {
        this.entryNumber = entryNumber;
    }
}
