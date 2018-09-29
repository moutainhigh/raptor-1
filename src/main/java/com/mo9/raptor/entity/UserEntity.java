package com.mo9.raptor.entity;

import com.mo9.raptor.engine.entity.AbstractStateEntity;
import com.mo9.raptor.engine.entity.IStateEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.enums.SourceEnum;
import com.mo9.raptor.utils.Md5Util;

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
    private Boolean mobileContacts = false;
    /**
     * 银行卡认证状态
     */
    @Column(name = "bank_auth_status")
    private String bankAuthStatus;
    /**
     * 认证信息是否上传并通过
     */
    @Column(name = "certify_info")
    private Boolean certifyInfo = false;
    /**
     * 是否设置银行卡 信息
     */
    @Column(name = "bank_card_set")
    private Boolean bankCardSet = false;
    /**
     * 通话记录是否授权
     */
    @Column(name = "call_history")
    private Boolean callHistory = false;

    @Column(name = "receive_call_history")
    private Boolean receiveCallHistory = false;

    @Column(name = "user_ip")
    private String userIp;

    @Column(name = "last_login_time")
    private Long lastLoginTime;
    /**
     * 提交认证时间
     */
    @Column(name = "auth_time")
    private Long authTime;

    /**
     * 注册来源
     */
    @Column(name = "source")
    private String source = SourceEnum.WHITE.name();

    /**
     * 子来源
     */
    @Column(name = "sub_source")
    private String subSource;

    /**
     * 手机通讯录完成时间
     */
    @Column(name = "mobile_contacts_time")
    private Long mobileContactsTime = -1L;

    /**
     * 身份信息完成时间
     */
    @Column(name = "certify_info_time")
    private Long certifyInfoTime = -1L;

    /**
     * 银行卡完成时间
     */
    @Column(name = "bank_card_set_time")
    private Long bankCardSetTime = -1L;

    /**
     * 通话记录授权时间
     */
    @Column(name = "call_history_time")
    private Long callHistoryTime = -1L;

    /**
     * 爬虫返回通话记录完成时间
     */
    @Column(name = "receive_call_history_time")
    private Long receiveCallHistoryTime = -1L;

    @Column(name = "living_body_certify")
    private Boolean livingBodyCertify = false;

    @Column(name = "login_enable")
    private Boolean loginEnable = true;

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

    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public void setAuthTime(Long authTime) {
        this.authTime = authTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getMobileContactsTime() {
        return mobileContactsTime;
    }

    public void setMobileContactsTime(Long mobileContactsTime) {
        this.mobileContactsTime = mobileContactsTime;
    }

    public Long getCertifyInfoTime() {
        return certifyInfoTime;
    }

    public void setCertifyInfoTime(Long certifyInfoTime) {
        this.certifyInfoTime = certifyInfoTime;
    }

    public Long getBankCardSetTime() {
        return bankCardSetTime;
    }

    public void setBankCardSetTime(Long bankCardSetTime) {
        this.bankCardSetTime = bankCardSetTime;
    }

    public Long getCallHistoryTime() {
        return callHistoryTime;
    }

    public void setCallHistoryTime(Long callHistoryTime) {
        this.callHistoryTime = callHistoryTime;
    }

    public Long getReceiveCallHistoryTime() {
        return receiveCallHistoryTime;
    }

    public void setReceiveCallHistoryTime(Long receiveCallHistoryTime) {
        this.receiveCallHistoryTime = receiveCallHistoryTime;
    }

    public Boolean getLivingBodyCertify() {
        return livingBodyCertify;
    }

    public void setLivingBodyCertify(Boolean livingBodyCertify) {
        this.livingBodyCertify = livingBodyCertify;
    }

    public Boolean getLoginEnable() {
        return loginEnable;
    }

    public void setLoginEnable(Boolean loginEnable) {
        this.loginEnable = loginEnable;
    }

    public String getSubSource() {
        return subSource;
    }

    public void setSubSource(String subSource) {
        this.subSource = subSource;
    }

    /**
     * 构建一个新用户
     * @param mobile
     * @param source
     * @return
     */
    public static UserEntity buildNewUser(String mobile, SourceEnum source){
        UserEntity userEntity = new UserEntity();
        userEntity.setMobile(mobile);
        userEntity.setUserCode(Md5Util.getMD5(mobile).toUpperCase());
        userEntity.setStatus(StatusEnum.COLLECTING.name());
        userEntity.setSource(source.name());
        long now = System.currentTimeMillis();
        userEntity.setCreateTime(now);
        userEntity.setUpdateTime(now);
        return userEntity;
    }
}
