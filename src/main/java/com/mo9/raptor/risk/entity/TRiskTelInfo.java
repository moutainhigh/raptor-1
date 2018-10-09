package com.mo9.raptor.risk.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 10:50 .
 */
@Entity
@Table(name = "t_risk_tel_info")
public class TRiskTelInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "sid")
    private String sid;

    @Column(name = "uid")
    private String uid;
    
    @Column(name = "full_name")
    private String fullName;
    
    @Column(name = "id_card")
    private String idCard;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "open_date")
    private String openDate;
    
    @Column(name = "report_received")
    private boolean reportReceived;
    
    @Column(name = "platform")
    private String platform;

    @Column(name = "field1")
    private String field1;

    @Column(name = "field2")
    private String field2;

    @Column(name = "field3")
    private String field3;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public boolean isReportReceived() {
        return reportReceived;
    }

    public void setReportReceived(boolean reportReceived) {
        this.reportReceived = reportReceived;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
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
