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
    EXCEPTION_CODE(11150001, "系统内部异常", "系统内部异常"),
    TEST_OPEN_CLOSE(11151002, "测试环境接口已经关闭", "测试环境接口已经关闭"),

    // xx=10 银行卡验证
    BANK_VERIFY_ERROR(11151001, "银行卡验证失败", "银行卡验证失败"),
    BANK_VERIFY_EXCEPTION(11151002, "银行卡验证超时", "银行卡验证超时"),
    BANK_CARD_NOT_EXIST(11151003,"银行卡查询不存在","银行卡查询不存在"),
    BANK_VERIFY_TOO_FREQUENTLY(11151004, "银行卡验证频繁 , 请稍后再试", "银行卡验证频繁 , 请稍后再试"),
    LOAN_BANK_LIST_NOT_SUPPORT(11151005, "借款银行卡列表不支持", "借款银行卡列表不支持"),

    // xx=20 验证码
    CAPTCHA_GET_TOO_OFTEN(11152001, "验证码获取过于频繁", "验证码获取过于频繁"),
    NOT_SUPPORT_CAPTCHA_TYPE(11152002, "不支持的验证码类型", "不支持的验证码类型"),
    CAPTCHA_IS_INVALID(11152003, "验证码已失效", "验证码已失效"),
    CAPTCHA_CHECK_ERROR(11152004, "验证码校验未通过", "验证码校验未通过"),
    CAPTCHA_NOT_NULL(11152005, "验证码不能为空", "验证码不能为空"),
    CAPTCHA_TOKEN_INVALID(11152006, "验证码token已失效", "验证码token已失效"),
    CAPTCHA_SEND_FAILED(11152007, "验证码发送失败", "验证码发送失败"),
    CAPTCHA_CHECK_TOO_OFTEN(11152008, "验证码校验过于频繁", "验证码校验过于频繁"),
    CAPTCHA_IS_INVALID_GRAPHIC(11152009,"图形验证码已失效,请重试" ,"图形验证码已失效,请重试" ),
    CAPTCHA_CHECK_ERROR_GRAPHIC(11152010, "图形验证码错误","图形验证码错误"),

            // xx=30 登录相关
    MOBILE_NOT_MEET_THE_REQUIRE(11153001, "手机号不符合要求", "手机号不符合要求"),
    NOT_WHITE_LIST_USER(11153002, "非白名单用户", "非白名单用户"),

    // xx=40 用户相关
    USER_CARD_ID_NOT_EXIST(11154000 , "用户身份证号不存在" , "用户身份证号不存在"),
    USER_NOT_EXIST(11154001 , "用户不存在" , "用户不存在"),
    IDCARD_IS_EXIST(11154002,"身份证已存在", "身份证已存在"),
    OCR_IDCARD_IS_EXIST(11154003,"ocr身份证已存在", "ocr身份证已存在"),
    CARD_CREDIT_IS_EXIST(11154004,"身份证信息已存在", "身份证信息已存在"),
    NOT_SUPPORT_TO_BLACK(11154005, "当前用户不支持拉黑", "当前用户不支持拉黑"),
    SIGN_CHECK_ERROR(11154006, "签名校验失败", "签名校验失败"),

    /**
     * xx=50, 还款
     */
    INVALID_RENEWAL_DAYS(11155000, "无效的延期天数", "无效的延期天数"),
    NO_REPAY_CHANNEL(11155001, "无效的还款渠道", "无效的还款渠道"),
    ILLEGAL_LOAN_ORDER_STATUE(11155003, "不合法的借款订单状态", "借款订单不可还款"),
    ILLEGAL_REPAYMENT(11155004, "非法的还款操作", "非法的还款操作"),
    GET_LOCK_FAILED(11155005, "锁竞争失败", "系统繁忙，请稍后重试"),
    PAY_INFO_EXPIRED(11155006, "支付信息已过期", "支付信息已过期"),
    CHANNEL_REPAY_FAILED(11155007, "渠道下单失败", "渠道下单失败"),
    INVALID_REPAY_INFO(11155008, "支付信息非法", "支付信息非法"),
    ERROR_BANK_CARD(11155009, "非法银行卡", "支付银行卡"),
    ILLEGAL_REQUEST_PRARM(11155010, "非法的请求参数", "非法的请求参数"),
    ILLEGAL_COUPON_AMOUNT(11155011, "非法的优惠金额", "非法的优惠金额"),
    MISMATCH_USER(11155012, "不匹配的用户和订单", "不匹配的用户和订单"),
    UNSUPPORTED_TYPE(11155013, "不支持的线下还款类型", "不支持的线下还款类型"),

    /**
     * xx=60, 借款
     */
    ONLY_ONE_ORDER(11156000, "不可同时借多笔订单", "不可同时借多笔订单"),
    ERROR_LOAN_PARAMS(11156001, "下单参数不正确", "下单参数不正确"),
    NO_LEND_AMOUNT(11156002, "今日额度已发放完毕，请明天尽早申请！", "今日无放款限额，请明天再来!"),
    PRODUCT_ERROR(11156003, "产品配置表错误", "系统繁忙，请稍后重试"),
    NO_LEND_INFO(11156004, "放款银行信息不存在", "放款失败"),
    LOAN_ORDER_NOT_EXISTED(11156005, "借款订单不存在", "借款订单不存在"),
    NO_LEND(11156006, "暂不支持借款", "暂不支持借款"),

    /**
     * xx=70,文件
     */
    FILE_SIZE_TOO_MAX(11157000, "文件超过大小限制", "文件超过大小限制"),

    /**
     * xx=80, 优惠券
     */
    EFFECTIVE_COUPON_EXISTED(11158000, "已存在可用优惠券", "已存在可用优惠券"),
    INVALID_COUPON_NUMBER(11158001, "优惠额度不合法", "优惠额度不合法"),
    INVALID_SIGN(11158002, "签名非法", "签名非法"),
    SIGN_PARAMS_EXTRACT_ERROR(11158003, "签名参数获取", "签名参数获取"),


    /**
     * xx=90, 现金账户
     */
    CASH_ACCOUNT_LOCK_FAILED(11159000, "现金账户获取锁失败", "现金账户获取锁失败"),
    CASH_ACCOUNT_BALANCE_LACK(11159001, "现金账户可用金额不够", "现金账户可用金额不够"),
    CASH_ACCOUNT_BUSINESS_NO_IS_EXIST(11159002, "现金账户业务流水号已经处理", "现金账户业务流水号已经处理"),
    CASH_ACCOUNT_EXCEPTION(11159003, "现金账户异常", "现金账户异常"),

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
