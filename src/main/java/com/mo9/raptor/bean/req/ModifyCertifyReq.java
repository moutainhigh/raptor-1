package com.mo9.raptor.bean.req;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 */
public class ModifyCertifyReq {

    /**
     *真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    /**
     *身份证
     */
    @NotBlank(message = "身份证不能为空")
    private String idCard;

    /**
     *发证机关
     */
    @NotBlank(message = "发证机关不能为空")
    private String issuingOrgan;

    /**
     *有效期开始时间
     */
    @NotBlank(message = "有效期开始时间不能为空")
    private String validityStartPeriod;

    /**
     *有效期结束时间
     */
    @NotBlank(message = "有效期结束时间不能为空")
    private String validityEndPeriod;

    /**
     *"0: 其他，1: 长期"
     */
    @NotNull(message = "type不能为空")
    private Integer type;

    /**
     *身份证正面照片地址
     */
    @NotBlank(message = "身份证正面照片地址不能为空")
    private String accountFrontImg;

    /**
     *身份证反面照片地址
     */
    @NotBlank(message = "身份证反面照片地址不能为空")
    private String accountBackImg;

    /**
     *账户ocr照片
     */
    @NotBlank(message = "账户ocr照片不能为空")
    private String accountOcr;

    /**
     *识别真实姓名
     */
    @NotBlank(message = "识别真实姓名不能为空")
    private String ocrRealName;

    /**
     *识别身份证
     */
    @NotBlank(message = "识别身份证不能为空")
    private String ocrIdCard;

    /**
     *识别签发地
     */
    @NotBlank(message = "识别签发地不能为空")
    private String ocrIssueAt;

    /**
     *识别身份有效期起始日期
     */
    @NotBlank(message = "别身份有效期起始日期不能为空")
    private String ocrDurationStartTime;

    /**
     *识别身份有效期结束日期
     */
    @NotBlank(message = "识别身份有效期结束日期不能为空")
    private String ocrDurationEndTime;

    /**
     *识别性别(0 : 男, 1 : 女, 2 : 未知)
     */
    @NotNull(message = "识别性别不能为空")
    private Integer ocrGender;

    /**
     *识别民族
     */
    @NotBlank(message = "识别民族不能为空")
    private String ocrNationality;

    /**
     *识别生日
     */
    @NotBlank(message = "识别生日不能为空")
    private String ocrBirthday;

    /**
     *识别地址
     */
    @NotBlank(message = "识别地址不能为空")
    private String ocrIdCardAddress;

    /**
     * 身份证正面扫描开始计数
     */
    @NotNull(message = "身份证正面扫描开始计数不能为空")
    private Integer frontStartCount;

    /**
     * 身份证正面扫描成功计数
     */
    @NotNull(message = "身份证正面扫描成功计数不能为空")
    private Integer frontSuccessCount;

    /**
     * 身份证正面扫描失败计数
     */
    @NotNull(message = "身份证正面扫描失败计数不能为空")
    private Integer frontFailCount;

    /**
     * 身份证背面扫描开始计数
     */
    @NotNull(message = "身份证背面扫描开始计数不能为空")
    private Integer backStartCount;

    /**
     * 身份证背面扫描成功计数
     */
    @NotNull(message = "身份证背面扫描成功计数不能为空")
    private Integer backSuccessCount;

    /**
     * 身份证背面扫描失败计数
     */
    @NotNull(message = "身份证背面扫描失败计数不能为空")
    private Integer backFailCount;

    /**
     * 活体扫描开始计数
     */
    @NotNull(message = "活体扫描开始计数不能为空")
    private Integer livenessStartCount;

    /**
     * 活体扫描成功计数
     */
    @NotNull(message = "活体扫描成功计数不能为空")
    private Integer livenessSuccessCount;

    /**
     * 活体扫描成功计数
     */
    @NotNull(message = "活体扫描成功计数不能为空")
    private Integer livenessFailCount;

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
