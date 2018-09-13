package com.mo9.raptor.entity;

import javax.persistence.*;

/**
 * @author zma
 * @date 2018/9/13
 */
@Entity
@Table(name = "t_raptor_user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_code")
    private String userCode;
    @Column(name = "mobile")
    private String mobile;

    @Column(name = "real_name")
    private String realName;

    @Column(name = "id_card")
    private String idCard;
    /**
     * 账户信用状态
     */
    @Column(name = "credit_status")
    private String creditStatus;
    /**
     * 审核状态
     */
    @Column(name = "audit_status")
    private String auditStatus;
    /**
     * 手机通讯录是否上传
     */
    @Column(name = "mobile_contacts")
    private Boolean mobileContacts;
    /**
     * 认证信息是否上传并通过
     */
    @Column(name = "certify_info")
    private Boolean certifyInfo;
    /**
     * 是否设置银行卡 信息
     */
    @Column(name = "bank_card_set")
    private Boolean bankCardSet;
    /**
     * 通话记录是否授权
     */
    @Column(name = "call_history")
    private Boolean callHistory;
    /**
     * 认证状态
     */
    @Column(name = "status")
    private String status;
    /**
     * 活体认证状态
     */
    @Column(name = "living_body_certify")
    private String livingBodyCertify;
    /**
     * 用户联系人信息文本
     */
    @Column(name = "mobile_contacts_text")
    private String mobileContactsText;
    /**
     * 用户通话记录文本
     */
    @Column(name = "call_history_text")
    private String call_history_text;

    @Column(name = "user_ip")
    private String userIp;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "last_login_time")
    private long lastLoginTime;
    /**
     * 提交认证时间
     */
    @Column(name = "auth_time")
    private long authTime;

    @Column(name = "update_time")
    private long updateTime;

    @Column(name = "create_time")
    private long createTime;

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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getCreditStatus() {
        return creditStatus;
    }

    public void setCreditStatus(String creditStatus) {
        this.creditStatus = creditStatus;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Boolean getMobileContacts() {
        return mobileContacts;
    }

    public void setMobileContacts(Boolean mobileContacts) {
        this.mobileContacts = mobileContacts;
    }

    public Boolean getCertifyInfo() {
        return certifyInfo;
    }

    public void setCertifyInfo(Boolean certifyInfo) {
        this.certifyInfo = certifyInfo;
    }

    public Boolean getBankCardSet() {
        return bankCardSet;
    }

    public void setBankCardSet(Boolean bankCardSet) {
        this.bankCardSet = bankCardSet;
    }

    public Boolean getCallHistory() {
        return callHistory;
    }

    public void setCallHistory(Boolean callHistory) {
        this.callHistory = callHistory;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLivingBodyCertify() {
        return livingBodyCertify;
    }

    public void setLivingBodyCertify(String livingBodyCertify) {
        this.livingBodyCertify = livingBodyCertify;
    }

    public String getMobileContactsText() {
        return mobileContactsText;
    }

    public void setMobileContactsText(String mobileContactsText) {
        this.mobileContactsText = mobileContactsText;
    }

    public String getCall_history_text() {
        return call_history_text;
    }

    public void setCall_history_text(String call_history_text) {
        this.call_history_text = call_history_text;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public long getAuthTime() {
        return authTime;
    }

    public void setAuthTime(long authTime) {
        this.authTime = authTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
