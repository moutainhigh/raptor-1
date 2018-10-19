package com.mo9.raptor.entity;

import javax.persistence.*;

/**
 * @author zma
 * @date 2018/9/29
 */
@Entity
@Table(name = "t_audit_operation_record")
public class AuditOperationRecordEntity extends BaseEntity {

    @Column(name = "user_code")
    private String userCode;

    @Column(name = "status")
    private String status;

    @Column(name = "operate_id")
    private String operateId;

    @Column(name = "distribute_id")
    private String distributeId;

    @Column(name = "audit_time")
    private Long auditTime;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOperateId() {
        return operateId;
    }

    public void setOperateId(String operateId) {
        this.operateId = operateId;
    }

    public String getDistributeId() {
        return distributeId;
    }

    public void setDistributeId(String distributeId) {
        this.distributeId = distributeId;
    }

    public Long getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Long auditTime) {
        this.auditTime = auditTime;
    }
}
