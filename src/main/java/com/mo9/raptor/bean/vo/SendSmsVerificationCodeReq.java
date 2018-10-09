package com.mo9.raptor.bean.vo;


import com.mo9.raptor.enums.CaptchaBusinessEnum;

import javax.validation.constraints.NotBlank;

/**
 * 发送短信
 * @author zma
 * @date 2018/7/16
 */
public class SendSmsVerificationCodeReq {

    private CaptchaBusinessEnum reason;

    @NotBlank(message = "手机号不能为空")
    private String mobile;

    private String graphCode;

    /**
     * 来源
     */
    private String source;

    /**
     * 子来源
     */
    private String subSource;

    public CaptchaBusinessEnum getReason() {
        return reason;
    }

    public void setReason(CaptchaBusinessEnum reason) {
        this.reason = reason;
    }

    public String getGraphCode() {
        return graphCode;
    }

    public void setGraphCode(String graphCode) {
        this.graphCode = graphCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSubSource() {
        return subSource;
    }

    public void setSubSource(String subSource) {
        this.subSource = subSource;
    }
}
