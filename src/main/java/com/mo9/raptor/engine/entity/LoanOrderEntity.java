package com.mo9.raptor.engine.entity;

import com.mo9.raptor.engine.enums.AuditModeEnum;
import com.mo9.raptor.engine.enums.LendModeEnum;
import com.mo9.raptor.engine.enums.NumberMode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 订单表 Created by gqwu on 2018/7/6.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_raptor_loan_order")
public class LoanOrderEntity extends AbstractOrderEntity {

    public boolean verify () {
        return true;
    }

    public BigDecimal canCoupon () {
        return BigDecimal.ZERO;
    }

    /** 借贷数目 */
    @Column(name = "loan_number")
    private BigDecimal loanNumber;

    /** 借贷期限 */
    @Column(name = "loan_term")
    private Integer loanTerm;

    /** 放款数目 */
    @Column(name = "lent_number")
    private BigDecimal lentNumber = BigDecimal.ZERO;

    /** 利息模式 - 默认固定利息 */
    @Column(name = "interest_mode")
    private String interestMode = NumberMode.QUANTITY.name();

    /** 利息模式下对应值 */
    @Column(name = "interest_value")
    private BigDecimal interestValue;

    /** 罚息模式 */
    @Column(name = "penalty_mode")
    private String penaltyMode = NumberMode.QUANTITY.name();

    /** 利息模式下对应值 */
    @Column(name = "penalty_value")
    private BigDecimal penaltyValue;

    /** 借贷服务费 */
    @Column(name = "charge_value")
    private BigDecimal chargeValue;

    /** 延期单位服务费 */
    @Column(name = "postpone_unit_charge")
    private BigDecimal postponeUnitCharge;

    /** 订单审核方式 */
    @Column(name = "audit_mode")
    private String auditMode = AuditModeEnum.AUTO.name();

    /** 审核者签名 - 给定审核结果的系统/用户 */
    @Column(name = "audit_signature")
    private String auditSignature = "";

    /** 审核时间 */
    @Column(name = "audit_time")
    private Long auditTime = -1L;

    /** 放款方式 - 自动放款/手动放款 */
    @Column(name = "lend_mode")
    private String lendMode = LendModeEnum.AUTO.name();

    /** 放款签名 - 执行放款的系统代码/用户ID */
    @Column(name = "lend_signature")
    private String lendSignature = "";

    /** 放款时间 */
    @Column(name = "lend_time")
    private Long lendTime = -1L;

    /** 还款日 */
    @Column(name = "repayment_date")
    private Long repaymentDate;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_version")
    private String clientVersion;

    /**
     * 延期次数
     */
    @Column(name = "postpone_count")
    private Integer postponeCount;

    /**
     * 还清时间
     */
    @Column(name = "payoff_time")
    private Long payoffTime;

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

    public String getAuditMode() {
        return auditMode;
    }

    public void setAuditMode(String auditMode) {
        this.auditMode = auditMode;
    }

    public String getAuditSignature() {
        return auditSignature;
    }

    public void setAuditSignature(String auditSignature) {
        this.auditSignature = auditSignature;
    }

    public Long getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Long auditTime) {
        this.auditTime = auditTime;
    }

    public String getLendMode() {
        return lendMode;
    }

    public void setLendMode(String lendMode) {
        this.lendMode = lendMode;
    }

    public String getLendSignature() {
        return lendSignature;
    }

    public void setLendSignature(String lendSignature) {
        this.lendSignature = lendSignature;
    }

    public Long getLendTime() {
        return lendTime;
    }

    public void setLendTime(Long lendTime) {
        this.lendTime = lendTime;
    }

    public Long getRepaymentDate() {
        return repaymentDate;
    }

    public void setRepaymentDate(Long repaymentDate) {
        this.repaymentDate = repaymentDate;
    }

    public BigDecimal getPostponeUnitCharge() {
        return postponeUnitCharge;
    }

    public void setPostponeUnitCharge(BigDecimal postponeUnitCharge) {
        this.postponeUnitCharge = postponeUnitCharge;
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

    public Integer getPostponeCount() {
        return postponeCount;
    }

    public void setPostponeCount(Integer postponeCount) {
        this.postponeCount = postponeCount;
    }

    public Long getPayoffTime() {
        return payoffTime;
    }

    public void setPayoffTime(Long payoffTime) {
        this.payoffTime = payoffTime;
    }
}
