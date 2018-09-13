package com.mo9.raptor.entity;

import javax.persistence.*;

/**
 * @author zma
 * @date 2018/9/13
 */
@Entity
@Table(name = "t_raptor_user")
public class UserCertifyInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "real_name")
    private String realName;
    @Column(name = "id_card")
    private String idCard;
    @Column(name = "issuing_organ")
    private String issuingOrgan;
    @Column(name = "validity_start_period")
    private String validityStartPeriod;
    @Column(name = "validity_end_period")
    private String validityEndPeriod;
    @Column(name = "type")
    private String type;
    @Column(name = "account_front_img")
    private String accountFrontImg;
    @Column(name = "account_back_img")
    private String accountBackImg;
    @Column(name = "account_ocr")
    private String accountOcr;
    @Column(name = "ocr_real_name")
    private String ocrRealName;
    @Column(name = "ocr_id_card")
    private String ocrIdCard;
    @Column(name = "ocr_issue_at")
    private String ocrIssueAt;
    @Column(name = "ocr_duration_start_time")
    private String ocrDurationStartTime;
    @Column(name = "ocr_duration_end_time")
    private String ocrDurationEndTime;
    @Column(name = "ocr_gender")
    private String ocrGender;
    @Column(name = "ocr_nationality")
    private String ocrNationality;
    @Column(name = "ocr_birthday")
    private String ocrBirthday;
    @Column(name = "ocr_id_card_address")
    private String ocrIdCardAddress;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getIssuingOrgan() {
        return issuingOrgan;
    }

    public void setIssuingOrgan(String issuingOrgan) {
        this.issuingOrgan = issuingOrgan;
    }

    public String getValidityStartPeriod() {
        return validityStartPeriod;
    }

    public void setValidityStartPeriod(String validityStartPeriod) {
        this.validityStartPeriod = validityStartPeriod;
    }

    public String getValidityEndPeriod() {
        return validityEndPeriod;
    }

    public void setValidityEndPeriod(String validityEndPeriod) {
        this.validityEndPeriod = validityEndPeriod;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccountFrontImg() {
        return accountFrontImg;
    }

    public void setAccountFrontImg(String accountFrontImg) {
        this.accountFrontImg = accountFrontImg;
    }

    public String getAccountBackImg() {
        return accountBackImg;
    }

    public void setAccountBackImg(String accountBackImg) {
        this.accountBackImg = accountBackImg;
    }

    public String getAccountOcr() {
        return accountOcr;
    }

    public void setAccountOcr(String accountOcr) {
        this.accountOcr = accountOcr;
    }

    public String getOcrRealName() {
        return ocrRealName;
    }

    public void setOcrRealName(String ocrRealName) {
        this.ocrRealName = ocrRealName;
    }

    public String getOcrIdCard() {
        return ocrIdCard;
    }

    public void setOcrIdCard(String ocrIdCard) {
        this.ocrIdCard = ocrIdCard;
    }

    public String getOcrIssueAt() {
        return ocrIssueAt;
    }

    public void setOcrIssueAt(String ocrIssueAt) {
        this.ocrIssueAt = ocrIssueAt;
    }

    public String getOcrDurationStartTime() {
        return ocrDurationStartTime;
    }

    public void setOcrDurationStartTime(String ocrDurationStartTime) {
        this.ocrDurationStartTime = ocrDurationStartTime;
    }

    public String getOcrDurationEndTime() {
        return ocrDurationEndTime;
    }

    public void setOcrDurationEndTime(String ocrDurationEndTime) {
        this.ocrDurationEndTime = ocrDurationEndTime;
    }

    public String getOcrGender() {
        return ocrGender;
    }

    public void setOcrGender(String ocrGender) {
        this.ocrGender = ocrGender;
    }

    public String getOcrNationality() {
        return ocrNationality;
    }

    public void setOcrNationality(String ocrNationality) {
        this.ocrNationality = ocrNationality;
    }

    public String getOcrBirthday() {
        return ocrBirthday;
    }

    public void setOcrBirthday(String ocrBirthday) {
        this.ocrBirthday = ocrBirthday;
    }

    public String getOcrIdCardAddress() {
        return ocrIdCardAddress;
    }

    public void setOcrIdCardAddress(String ocrIdCardAddress) {
        this.ocrIdCardAddress = ocrIdCardAddress;
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
}
