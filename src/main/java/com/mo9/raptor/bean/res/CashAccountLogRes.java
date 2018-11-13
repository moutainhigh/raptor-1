package com.mo9.raptor.bean.res;


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
public class CashAccountLogRes {

    /**
     * id
     */
    private Long id ;

    /**
     * 数量
     */
    private BigDecimal balanceChange ;


    /**
     * 变化之前数量 -- 可用数量变化
     */
    private BigDecimal beforeBalance ;

    /**
     * 变化之后数量 -- 可用数量变化
     */
    private BigDecimal afterBalance ;

    /**
     * 账户出账入账类型  in 入 out 出
     */
    private String balanceType ;

    /**
     * 引起变化订单类型
     */
    private String type ;

    /**
     * 创建时间
     */
    private Long createTime ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(String balanceType) {
        this.balanceType = balanceType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
