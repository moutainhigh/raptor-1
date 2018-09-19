package com.mo9.raptor.entity;

import javax.persistence.*;

/**
 * @author zma
 * @date 2018/9/13
 */
@Entity
@Table(name = "t_raptor_user_certify_info")
public class UserCertifyInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_code")
    private String userCode;

    /**
     * 真实姓名
     */
    @Column(name = "real_name")
    private String realName;

    /**
     * 身份证
     */
    @Column(name = "id_card")
    private String idCard;

    /**
     * 识别地址
     */
    @Column(name = "issuing_organ")
    private String issuingOrgan;

    /**
     * 有效期开始时间
     */
    @Column(name = "validity_start_period")
    private String validityStartPeriod;

    /**
     * 有效期结束时间
     */
    @Column(name = "validity_end_period")
    private String validityEndPeriod;

    /**
     * 0: 其他，1: 长期
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 身份证正面照片地址
     */
    @Column(name = "account_front_img")
    private String accountFrontImg;

    /**
     * 身份证背面照片地址
     */
    @Column(name = "account_back_img")
    private String accountBackImg;

    /**
     * 账户ocr照片
     */
    @Column(name = "account_ocr")
    private String accountOcr;

    /**
     * ocr识别姓名
     */
    @Column(name = "ocr_real_name")
    private String ocrRealName;

    /**
     * orc识别身份证号
     */
    @Column(name = "ocr_id_card")
    private String ocrIdCard;

    /**
     * 识别签发地
     */
    @Column(name = "ocr_issue_at")
    private String ocrIssueAt;

    /**
     * orc识别有效开始日期
     */
    @Column(name = "ocr_duration_start_time")
    private String ocrDurationStartTime;

    /**
     * orc识别有效结束日期
     */
    @Column(name = "ocr_duration_end_time")
    private String ocrDurationEndTime;

    /**
     * orc识别性别
     */
    @Column(name = "ocr_gender")
    private Integer ocrGender;

    /**
     * orc识别民族
     */
    @Column(name = "ocr_nationality")
    private String ocrNationality;

    /**
     * orc识别生日
     */
    @Column(name = "ocr_birthday")
    private String ocrBirthday;

    /**
     * orc识别身份地址
     */
    @Column(name = "ocr_id_card_address")
    private String ocrIdCardAddress;

    /**
     * 身份证正面扫描开始计数
     */
    @Column(name = "front_start_count")
    private Integer frontStartCount = 0;

    /**
     * 身份证正面扫描成功计数
     */
    @Column(name = "front_success_count")
    private Integer frontSuccessCount = 0;

    /**
     * 身份证正面扫描失败计数
     */
    @Column(name = "front_fail_count")
    private Integer frontFailCount = 0;

    /**
     * 身份证背面扫描开始计数
     */
    @Column(name = "back_start_count")
    private Integer backStartCount = 0;

    /**
     * 身份证背面扫描成功计数
     */
    @Column(name = "back_success_count")
    private Integer backSuccessCount = 0;

    /**
     * 身份证背面扫描失败计数
     */
    @Column(name = "back_fail_count")
    private Integer backFailCount = 0;

    /**
     * 活体扫描开始计数
     */
    @Column(name = "liveness_start_count")
    private Integer livenessStartCount = 0;

    /**
     * 活体扫描成功计数
     */
    @Column(name = "liveness_success_count")
    private Integer livenessSuccessCount = 0;

    /**
     * 活体扫描成功计数
     */
    @Column(name = "liveness_fail_count")
    private Integer livenessFailCount = 0;

    @Column(name = "update_time")
    private Long updateTime;

    @Column(name = "create_time")
    private Long createTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
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

    public Integer getOcrGender() {
        return ocrGender;
    }

    public void setOcrGender(Integer ocrGender) {
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

    public Integer getFrontStartCount() {
        return frontStartCount;
    }

    public void setFrontStartCount(Integer frontStartCount) {
        this.frontStartCount = frontStartCount;
    }

    public Integer getFrontSuccessCount() {
        return frontSuccessCount;
    }

    public void setFrontSuccessCount(Integer frontSuccessCount) {
        this.frontSuccessCount = frontSuccessCount;
    }

    public Integer getFrontFailCount() {
        return frontFailCount;
    }

    public void setFrontFailCount(Integer frontFailCount) {
        this.frontFailCount = frontFailCount;
    }

    public Integer getBackStartCount() {
        return backStartCount;
    }

    public void setBackStartCount(Integer backStartCount) {
        this.backStartCount = backStartCount;
    }

    public Integer getBackSuccessCount() {
        return backSuccessCount;
    }

    public void setBackSuccessCount(Integer backSuccessCount) {
        this.backSuccessCount = backSuccessCount;
    }

    public Integer getBackFailCount() {
        return backFailCount;
    }

    public void setBackFailCount(Integer backFailCount) {
        this.backFailCount = backFailCount;
    }

    public Integer getLivenessStartCount() {
        return livenessStartCount;
    }

    public void setLivenessStartCount(Integer livenessStartCount) {
        this.livenessStartCount = livenessStartCount;
    }

    public Integer getLivenessSuccessCount() {
        return livenessSuccessCount;
    }

    public void setLivenessSuccessCount(Integer livenessSuccessCount) {
        this.livenessSuccessCount = livenessSuccessCount;
    }

    public Integer getLivenessFailCount() {
        return livenessFailCount;
    }

    public void setLivenessFailCount(Integer livenessFailCount) {
        this.livenessFailCount = livenessFailCount;
    }
}
