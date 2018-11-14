package com.mo9.raptor.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xtgu on 2018/10/31.
 * @author xtgu
 * 现金账户
 */
@Entity
@Table(name = "t_raptor_cash_account")
public class CashAccountEntity {

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
     * 可用数量
     */
    @Column(name = "balance")
    private BigDecimal balance ;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime ;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime ;

    /**
     * 是否删除
     */
    @Column(name = "deleted")
    private Boolean deleted ;


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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
