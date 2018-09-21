package com.mo9.raptor.bean.res;

import javax.persistence.Column;

/**
 * mq用户信息
 * Created by xzhang on 2018/9/20.
 */
public class UserInfoMqRes {

    private String userCode;

    private String mobile;


    private String realName;


    private String idCard;

    /**
     * 审核状态
     */
    private String creditStatus;

    private String userIp;

    private Long lastLoginTime;

    /**
     * 证件地址
     */
    private String ocrIdCardAddress;

    /**
     * 通话记录是否授权
     */
    private Boolean callHistory;

    /**
     * 通讯录
     */
    private String contactsList;

    /**
     * 是否已删除
     */
    private Boolean deleted;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
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

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public Long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getOcrIdCardAddress() {
        return ocrIdCardAddress;
    }

    public void setOcrIdCardAddress(String ocrIdCardAddress) {
        this.ocrIdCardAddress = ocrIdCardAddress;
    }

    public Boolean getCallHistory() {
        return callHistory;
    }

    public void setCallHistory(Boolean callHistory) {
        this.callHistory = callHistory;
    }

    public String getContactsList() {
        return contactsList;
    }

    public void setContactsList(String contactsList) {
        this.contactsList = contactsList;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
