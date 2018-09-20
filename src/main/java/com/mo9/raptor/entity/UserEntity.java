package com.mo9.raptor.entity;

import com.mo9.raptor.engine.entity.AbstractStateEntity;
import com.mo9.raptor.engine.entity.IStateEntity;

import javax.persistence.*;

/**
 * @author zma
 * @date 2018/9/13
 */
@Entity
@Table(name = "t_raptor_user")
public class UserEntity extends AbstractStateEntity implements IStateEntity {

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
     * 手机通讯录是否上传
     */
    @Column(name = "mobile_contacts")
    private Boolean mobileContacts;
    /**
     * 银行卡认证状态
     */
    @Column(name = "bank_auth_status")
    private String bankAuthStatus;
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
    private String callHistoryText;

    @Column(name = "receive_call_history")
    private Boolean receiveCallHistory;

    @Column(name = "user_ip")
    private String userIp;

    @Column(name = "last_login_time")
    private Long lastLoginTime;
    /**
     * 提交认证时间
     */
    @Column(name = "auth_time")
    private Long authTime;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBankAuthStatus() {
        return bankAuthStatus;
    }

    public void setBankAuthStatus(String bankAuthStatus) {
        this.bankAuthStatus = bankAuthStatus;
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

    public Boolean getReceiveCallHistory() {
        return receiveCallHistory;
    }

    public void setReceiveCallHistory(Boolean receiveCallHistory) {
        this.receiveCallHistory = receiveCallHistory;
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

    public String getCallHistoryText() {
        return callHistoryText;
    }

    public void setCallHistoryText(String callHistoryText) {
        this.callHistoryText = callHistoryText;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
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

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
