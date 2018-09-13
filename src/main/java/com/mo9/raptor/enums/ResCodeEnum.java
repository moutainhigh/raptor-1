package com.mo9.raptor.enums;

/**
 * @author jyou
 */
public enum ResCodeEnum {

    /**
     * 包含所有需要返回的错误码，请及时补充注释，其实格式如下：
     * 1115xxyy，1115：代表业务系统， xx代表业务模块， yy：代表具体错误码
     * <p>
     */
    SUCCESS(11150000, "成功", "成功"),
    EXCEPTION_CODE(11150001, "成功", "成功"),

    // xx=10 银行卡验证
    BANK_VERIFY_ERROR(11151001, "银行卡验证失败", "银行卡验证失败"),
    BANK_VERIFY_EXCEPTION(11151002, "银行卡验证超时", "银行卡验证超时"),

    // xx=20 验证码
    CAPTCHA_GET_TOO_OFTEN(11152001, "验证码获取过于频繁", "验证码获取过于频繁"),
    NOT_SUPPORT_CAPTCHA_TYPE(11152002, "不支持的验证码类型", "不支持的验证码类型"),
    CAPTCHA_IS_INVALID(11152003, "验证码已失效", "验证码已失效"),
    CAPTCHA_CHECK_ERROR(11152004, "验证码校验未通过", "验证码校验未通过"),
    CAPTCHA_NOT_NULL(11152005, "验证码不能为空", "验证码不能为空"),
    CAPTCHA_TOKEN_INVALID(11152006, "验证码token已失效", "验证码token已失效"),
    CAPTCHA_SEND_FAILED(11152007, "验证码发送失败", "验证码发送失败"),
    CAPTCHA_CHECK_TOO_OFTEN(11152008, "验证码校验过于频繁", "验证码校验过于频繁"),

    // xx=30 登录相关
    MOBILE_NOT_MEET_THE_REQUIRE(11153001, "手机号不符合要求", "手机号不符合要求"),
    NOT_WHITE_LIST_USER(11153002, "非白名单用户", "非白名单用户"),

    ;
    /**
     * 错误码
     */
    private int code;

    /**
     * 注释说明, 用于实际说明问题, 与code一对一, 不可重复
     */
    private String explain;

    /**
     * 描述, 用于返回给前端, 可以重复
     */
    private String message;

    ResCodeEnum(int code, String explain, String message) {
        this.code = code;
        this.explain = explain;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getExplain() {
        return explain;
    }

    public String getMessage() {
        return message;
    }
}
