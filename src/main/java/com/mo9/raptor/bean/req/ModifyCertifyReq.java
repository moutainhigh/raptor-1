package com.mo9.raptor.bean.req;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 */
public class ModifyCertifyReq {

    /**
     *真实姓名
     */
    private String realName;

    /**
     *身份证
     */
    private String idCard;

    /**
     *发证机关
     */
    private String issuingOrgan;

    /**
     *有效期开始时间
     */
    private String validityStartPeriod;

    /**
     *有效期结束时间
     */
    private String validityEndPeriod;

    /**
     *"0: 其他，1: 长期"
     */
    private Integer type;

    /**
     *身份证正面照片地址
     */
    private String accountFrontImg;

    /**
     *身份证反面照片地址
     */
    private String accountBackImg;

    /**
     *账户ocr照片
     */
    private String accountOcr;

    /**
     *识别真实姓名
     */
    private String ocrRealName;

    /**
     *识别身份证
     */
    private String ocrIdCard;

    /**
     *识别签发地
     */
    private String ocrIssueAt;

    /**
     *识别身份有效期起始日期
     */
    private String ocrDurationStartTime;

    /**
     *识别身份有效期结束日期
     */
    private String ocrDurationEndTime;

    /**
     *识别性别(0 : 男, 1 : 女, 2 : 未知)
     */
    private Integer ocrGender;

    /**
     *识别民族
     */
    private String ocrNationality;

    /**
     *识别生日
     */
    private String ocrBirthday;

    /**
     *识别地址
     */
    private String ocrIdCardAddress;

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
}
