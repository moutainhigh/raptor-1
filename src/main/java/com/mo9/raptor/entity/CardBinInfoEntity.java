package com.mo9.raptor.entity;

import javax.persistence.*;

/**
 * Created by jyou on 2018/9/18.
 *
 * @author jyou
 */
@Entity
@Table(name = "t_raptor_card_bin_info")
public class CardBinInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_prefix")
    private String cardPrefix;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "card_bank")
    private String cardBank;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "issupport")
    private Boolean issupport;

    @Column(name = "create_time")
    private Long createTime;

    @Column(name = "remark")
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardPrefix() {
        return cardPrefix;
    }

    public void setCardPrefix(String cardPrefix) {
        this.cardPrefix = cardPrefix;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardBank() {
        return cardBank;
    }

    public void setCardBank(String cardBank) {
        this.cardBank = cardBank;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public Boolean getIssupport() {
        return issupport;
    }

    public void setIssupport(Boolean issupport) {
        this.issupport = issupport;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
