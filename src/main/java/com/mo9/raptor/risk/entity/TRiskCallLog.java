package com.mo9.raptor.risk.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 10:31 .
 */

@Entity
@Table(name = "t_risk_call_log")
public class TRiskCallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "mobile")
    private String mobile;
    
    @Column(name = "sid")
    private String sid;
    
    @Column(name = "uid")
    private String uid;
    
    @Column(name = "call_from")
    private String callFrom;
    
    @Column(name = "call_to")
    private String callTo;
    
    @Column(name = "call_tel")
    private String callTel;
    
    @Column(name = "call_method")
    private String callMethod;
    
    @Column(name = "call_type")
    private String callType;
    
    @Column(name = "call_cost")
    private String callCost;
    
    @Column(name = "call_duration")
    private String callDuration;
    
    @Column(name = "call_time")
    private String callTime;
    
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
    

    public String getCallFrom() {
        return callFrom;
    }

    public void setCallFrom(String callFrom) {
        this.callFrom = callFrom;
    }

    public String getCallTo() {
        return callTo;
    }

    public void setCallTo(String callTo) {
        this.callTo = callTo;
    }

    public String getCallTel() {
        return callTel;
    }

    public void setCallTel(String callTel) {
        this.callTel = callTel;
    }

    public String getCallMethod() {
        return callMethod;
    }

    public void setCallMethod(String callMethod) {
        this.callMethod = callMethod;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallCost() {
        return callCost;
    }

    public void setCallCost(String callCost) {
        this.callCost = callCost;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
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
