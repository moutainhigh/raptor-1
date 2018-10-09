package com.mo9.raptor.bean.req;



import javax.validation.constraints.NotBlank;

/**
 * 发送短信
 * @author zma
 * @date 2018/7/16
 */
public class LoginByCodeReq {

    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @NotBlank(message = "验证码不能为空")
    private String code;

    /**
     * 图形验证码
     */
    private String captcha;

    /**
     * 来源
     */
    private String source;

    /**
     * 子来源
     */
    private String subSource;

    /**
     * 图形验证码标识
     */
    private String captchaKey;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
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

    public String getCaptchaKey() {
        return captchaKey;
    }

    public void setCaptchaKey(String captchaKey) {
        this.captchaKey = captchaKey;
    }
}
