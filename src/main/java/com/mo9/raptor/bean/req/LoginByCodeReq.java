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
}
