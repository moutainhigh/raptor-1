package com.mo9.raptor.bean.res;

import javax.persistence.Column;

/**
 * mq用户信息
 * Created by xzhang on 2018/9/20.
 */
public class UserInfoMqRes {

    /**
     * 产品类型
     */
    private String productType;

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
     * 性别
     */
    private Integer gender;

    /**
     * 注册来源
     */
    private String source ;

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

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
