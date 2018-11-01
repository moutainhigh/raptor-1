package com.mo9.raptor.entity;


import com.mo9.raptor.enums.BalanceTypeEnum;
import com.mo9.raptor.enums.BusinessTypeEnum;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xtgu on 2018/7/5.
 * @author xtgu
 * 现金账户日志表
 */
@Entity
@Table(name = "t_raptor_cash_account_log")
public class CashAccountLogEntity {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    /**
     * 用户唯一标识
     */
    @Column(name = "user_code")
    private String userCode;

    /**
     * 数量
     */
    @Column(name = "balance_change")
    private BigDecimal balanceChange ;


    /**
     * 变化之前数量 -- 可用数量变化
     */
    @Column(name = "before_balance")
    private BigDecimal beforeBalance ;

    /**
     * 变化之后数量 -- 可用数量变化
     */
    @Column(name = "after_balance")
    private BigDecimal afterBalance ;

    /**
     * 账户出账入账类型  in 入 out 出
     */
    @Column(name = "balance_type")
    @Enumerated(EnumType.STRING)
    private BalanceTypeEnum balanceType ;

    /**
     * 引起变化订单
     */
    @Column(name = "business_no")
    private String businessNo ;

    /**
     * 引起变化订单类型
     */
    @Column(name = "business_type")
    @Enumerated(EnumType.STRING)
    private BusinessTypeEnum businessType ;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime ;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public BigDecimal getBalanceChange() {
        return balanceChange;
    }

    public void setBalanceChange(BigDecimal balanceChange) {
        this.balanceChange = balanceChange;
    }

    public BigDecimal getBeforeBalance() {
        return beforeBalance;
    }

    public void setBeforeBalance(BigDecimal beforeBalance) {
        this.beforeBalance = beforeBalance;
    }

    public BigDecimal getAfterBalance() {
        return afterBalance;
    }

    public void setAfterBalance(BigDecimal afterBalance) {
        this.afterBalance = afterBalance;
    }

    public BalanceTypeEnum getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(BalanceTypeEnum balanceType) {
        this.balanceType = balanceType;
    }

    public String getBusinessNo() {
        return businessNo;
    }

    public void setBusinessNo(String businessNo) {
        this.businessNo = businessNo;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
