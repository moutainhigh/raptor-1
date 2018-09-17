package com.mo9.raptor.risk.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 10:43 .
 */

@Entity
@Table(name = "t_risk_tel_bill")
public class TRiskTelBill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "sid")
    private String sid;

    @Column(name = "uid")
    private String uid;
    
    @Column(name = "bill_month")
    private String billMonth;
    
    @Column(name = "bill_amount")
    private String billAmount;
    
    @Column(name = "bill_package")
    private String billPackage;
    
    @Column(name = "bill_ext_calls")
    private String billExtCalls;
    
    @Column(name = "bill_ext_data")
    private String billExtData;
    
    @Column(name = "bill_ext_sms")
    private String billExtSms;
    
    @Column(name = "bill_zengzhifei")
    private String billZengzhifei;
    
    @Column(name = "bill_daishoufei")
    private String billDaishoufei;
    
    @Column(name = "bill_qita")
    private String billQita;

    @Column(name = "created_at")
    private Date createdAt = new Date();

    @Column(name = "updated_at")
    private Date updatedAt = new Date();

    @Column(name = "deleted")
    private boolean deleted = false;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBillMonth() {
        return billMonth;
    }

    public void setBillMonth(String billMonth) {
        this.billMonth = billMonth;
    }

    public String getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(String billAmount) {
        this.billAmount = billAmount;
    }

    public String getBillPackage() {
        return billPackage;
    }

    public void setBillPackage(String billPackage) {
        this.billPackage = billPackage;
    }

    public String getBillExtCalls() {
        return billExtCalls;
    }

    public void setBillExtCalls(String billExtCalls) {
        this.billExtCalls = billExtCalls;
    }

    public String getBillExtData() {
        return billExtData;
    }

    public void setBillExtData(String billExtData) {
        this.billExtData = billExtData;
    }

    public String getBillExtSms() {
        return billExtSms;
    }

    public void setBillExtSms(String billExtSms) {
        this.billExtSms = billExtSms;
    }

    public String getBillZengzhifei() {
        return billZengzhifei;
    }

    public void setBillZengzhifei(String billZengzhifei) {
        this.billZengzhifei = billZengzhifei;
    }

    public String getBillDaishoufei() {
        return billDaishoufei;
    }

    public void setBillDaishoufei(String billDaishoufei) {
        this.billDaishoufei = billDaishoufei;
    }

    public String getBillQita() {
        return billQita;
    }

    public void setBillQita(String billQita) {
        this.billQita = billQita;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
