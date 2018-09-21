package com.mo9.raptor.entity;

import javax.persistence.*;

/**
 * @author wtwei .
 * @date 2018/9/20 .
 * @time 18:54 .
 */
@Entity
@Table(name = "t_risk_dianhuabang_api_log")
public class DianHuaBangApiLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status")
    private Long status;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "platform")
    private String platform;

    @Column(name = "sid")
    private String sid;

    @Column(name = "uid")
    private String uid;
    
    

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    @Column(name = "create_time")
    private Long createTime = System.currentTimeMillis();

    @Column(name = "update_time")
    private Long updateTime = System.currentTimeMillis();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
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

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
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
}
