package com.mo9.raptor.entity;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by xtgu on 2018/9/16.
 * @author xtgu
 * 产品配置表
 */
@Entity
@Table(name = "t_raptor_loan_product")
public class LoanProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    /**
     * 金额
     */
    @Column(name = "amount")
    private BigDecimal amount ;

    /**
     * 期限
     */
    @Column(name = "period")
    private Integer period ;

    /**
     * 利息
     */
    @Column(name = "interest")
    private BigDecimal interest ;

    /**
     * 延期费用
     */
    @Column(name = "renewal_amount")
    private BigDecimal renewalAmount ;

    /**
     * 实际到账金额
     */
    @Column(name = "actually_get_amount")
    private BigDecimal actuallyGetAmount ;


    /**
     * 类型
     */
    @Column(name = "is_delete")
    private Boolean isDelete ;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Long createTime ;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Long updateTime ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public BigDecimal getActuallyGetAmount() {
        return actuallyGetAmount;
    }

    public void setActuallyGetAmount(BigDecimal actuallyGetAmount) {
        this.actuallyGetAmount = actuallyGetAmount;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

    public BigDecimal getRenewalAmount() {
        return renewalAmount;
    }

    public void setRenewalAmount(BigDecimal renewalAmount) {
        this.renewalAmount = renewalAmount;
    }
}
