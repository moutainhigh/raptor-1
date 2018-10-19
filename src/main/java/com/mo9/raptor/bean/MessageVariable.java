package com.mo9.raptor.bean;

/**
 * 发送邮件或短信消息所需要的变量
 * @author zma
 * @date 2018/7/25
 */
public interface MessageVariable {
    /**
     * 公司名称
     */
    String COMPANY = "XXX有限公司";
    /**
     * 公司简称名称
     */
    String SIMPLE_COMPANY = "XXX";

    /**
     * 产品名称
     */
    String RAPTOR_SIGN_NAME = "天天有钱";

    /**
     * 服务邮箱
     */
    String SERVICE_MAILBOX = "service@ttyq.com";
    /**
     * 主题
     */
    String SUBJECT = "subject";
    /**
     * 短信签名
     */
    String SIGN = "sign";
    /**
     * 验证码
     */
    String CAPTCHA = "pinCode";
    /**
     * 币种
     */
    String CURRENCY = "currency";
    /**
     * 金额
     */
    String AMOUNT = "amount";
    /**
     * 同一个模版含有两个相同变量用A或B区分
     */
    String AMOUNT_A = "amountA";
    /**
     * 同一个模版含有两个相同变量用A或B区分
     */
    String AMOUNT_B = "amountB";
    /**
     * 时间
     */
    String TIME = "time";

}
