package com.mo9.raptor.bean.res;

/**
 * 获取账户审核状态
 * @author zma
 * @date 2018/9/17
 */
public class AuditStatusRes {
    /**
     * 审核状态 -1失败 0未认证 1审核中 2通过 3拉黑
     */
    private String auditStatus;
    /**
     * 认证信息（OCR）有没有上传并通过
     */
    private Boolean certifyInfo;
    /**
     *手机通讯录有没有上传
     */
    private Boolean mobileContacts;
    /**
     * 通话记录有没有爬过
     */
    private Boolean callHistory;
    /**
     * 银行四要素认证是否通过
     */
    private Boolean accountBankCardVerified;
    /**
     * 银行卡有没有设置, 有的话返回银行卡信息，没的话返回null
     */
    private AccountBankCardRes accountBankCard;

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Boolean getCertifyInfo() {
        return certifyInfo;
    }

    public void setCertifyInfo(Boolean certifyInfo) {
        this.certifyInfo = certifyInfo;
    }

    public Boolean getMobileContacts() {
        return mobileContacts;
    }

    public void setMobileContacts(Boolean mobileContacts) {
        this.mobileContacts = mobileContacts;
    }

    public Boolean getCallHistory() {
        return callHistory;
    }

    public void setCallHistory(Boolean callHistory) {
        this.callHistory = callHistory;
    }

    public Boolean getAccountBankCardVerified() {
        return accountBankCardVerified;
    }

    public void setAccountBankCardVerified(Boolean accountBankCardVerified) {
        this.accountBankCardVerified = accountBankCardVerified;
    }

    public AccountBankCardRes getAccountBankCard() {
        return accountBankCard;
    }

    public void setAccountBankCard(AccountBankCardRes accountBankCard) {
        this.accountBankCard = accountBankCard;
    }
}
