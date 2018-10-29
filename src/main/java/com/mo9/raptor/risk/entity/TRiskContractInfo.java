package com.mo9.raptor.risk.entity;

import javax.persistence.*;

/**
 * Created by jyou on 2018/10/18.
 *
 * @author jyou
 */
@Entity
@Table(name = "t_risk_contract_info")
public class TRiskContractInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "user_code")
    private String userCode;

    @Column(name = "contact_name")
    private String contractName;

    @Column(name = "contact_mobile")
    private String contractMobile;

    @Column(name = "contact_relationship")
    private String contractRelationship;

    @Column(name = "contact_type")
    private String contractType;

    @Column(name = "remark")
    private String remark;

    @Column(name = "is_matching")
    private Boolean isMatching = false;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getContractMobile() {
        return contractMobile;
    }

    public void setContractMobile(String contractMobile) {
        this.contractMobile = contractMobile;
    }

    public String getContractRelationship() {
        return contractRelationship;
    }

    public void setContractRelationship(String contractRelationship) {
        this.contractRelationship = contractRelationship;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
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

    public Boolean getMatching() {
        return isMatching;
    }

    public void setMatching(Boolean matching) {
        isMatching = matching;
    }
}
