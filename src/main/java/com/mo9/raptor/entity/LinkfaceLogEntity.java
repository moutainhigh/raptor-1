package com.mo9.raptor.entity;

import javax.persistence.*;

@Entity
@Table(name = "t_raptor_linkface_log")
public class LinkfaceLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 调用参数
     */
    @Column(name = "call_params")
    private String callParams;


    /**
     * 调用结果
     */
    @Column(name = "call_result")
    private String callResult;


    /**
     * 状态
     */
    @Column(name = "status")
    private String status;

    /**
     * 状态
     */
    @Column(name = "user_code")
    private String userCode;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    @Column(name = "create_time")
    private Long createTime;

    @Column(name = "update_time")
    private Long updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCallParams() {
        return callParams;
    }

    public void setCallParams(String callParams) {
        this.callParams = callParams;
    }

    public String getCallResult() {
        return callResult;
    }

    public void setCallResult(String callResult) {
        this.callResult = callResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
