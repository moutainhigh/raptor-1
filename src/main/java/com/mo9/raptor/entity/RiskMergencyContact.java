package com.mo9.raptor.entity;

import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * @author wtwei .
 * @date 2018/10/8 .
 * @time 14:25 .
 * 
 * 紧急联系人
 */

@Entity
@Table(name = "t_risk_mergency_contact")
public class RiskMergencyContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "mobile")
    private String mobile;
    
    @Column(name = "contact_tel")
    private String contractTel;
    
    @Column(name = "tags_label")
    private String tagsLabel;
    
    @Column(name = "tags_label_times")
    private Integer tagsLabelTimes;
    
    @Column(name = "contact_name")
    private String contactName;
    
    @Column(name = "contact_relationship")
    private String contactRelationship;
    
    @Column(name = "tags_financial")
    private String tagsFinancial;
    
    @Column(name = "contact_priority")
    private Integer contactPriority;
    
    @Column(name = "fancha_telloc")
    private String fanchaTelloc;
    
    @Column(name = "call_length")
    private String callLength;
    
    @Column(name = "call_times")
    private String callTimes;
    
    @Column(name = "tags_yellow_page")
    private String tagsYellowPage;

    @Column(name = "update_time")
    private Long updateTime = System.currentTimeMillis();

    @Column(name = "create_time")
    private Long createTime = System.currentTimeMillis();


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

    public String getContractTel() {
        return contractTel;
    }

    public void setContractTel(String contractTel) {
        this.contractTel = contractTel;
    }

    public String getTagsLabel() {
        return tagsLabel;
    }

    public void setTagsLabel(String tagsLabel) {
        this.tagsLabel = tagsLabel;
    }

    public Integer getTagsLabelTimes() {
        return tagsLabelTimes;
    }

    public void setTagsLabelTimes(Integer tagsLabelTimes) {
        this.tagsLabelTimes = tagsLabelTimes;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactRelationship() {
        return contactRelationship;
    }

    public void setContactRelationship(String contactRelationship) {
        this.contactRelationship = contactRelationship;
    }

    public String getTagsFinancial() {
        return tagsFinancial;
    }

    public void setTagsFinancial(String tagsFinancial) {
        this.tagsFinancial = tagsFinancial;
    }

    public Integer getContactPriority() {
        return contactPriority;
    }

    public void setContactPriority(Integer contactPriority) {
        this.contactPriority = contactPriority;
    }

    public String getFanchaTelloc() {
        return fanchaTelloc;
    }

    public void setFanchaTelloc(String fanchaTelloc) {
        this.fanchaTelloc = fanchaTelloc;
    }

    public String getCallLength() {
        return callLength;
    }

    public void setCallLength(String callLength) {
        this.callLength = callLength;
    }

    public String getCallTimes() {
        return callTimes;
    }

    public void setCallTimes(String callTimes) {
        this.callTimes = callTimes;
    }

    public String getTagsYellowPage() {
        return tagsYellowPage;
    }

    public void setTagsYellowPage(String tagsYellowPage) {
        this.tagsYellowPage = tagsYellowPage;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
