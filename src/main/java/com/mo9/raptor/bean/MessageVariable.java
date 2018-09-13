package com.mo9.libracredit.bean;

/**
 * 发送邮件或短信消息所需要的变量
 * @author zma
 * @date 2018/7/25
 */
public interface MessageVariable {
    /**
     * 产品名称
     */
    String  LIBRA_CREDIT = "Libra Credit";
    /**
     * 系统码
     */
    String  SYSTEM_CODE = "LIBRA";
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
    String CAPTCHA = "captcha";
    /**
     * 币种
     */
    String CURRENCY = "currency";
    /**
     * 金额（包含数字和币种名称,示例：1.255000 ETH）
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
     * 时间 示例： 2018-03-14 23:59:59（UTC）
     */
    String TIME = "time";
    /**
     * 用户名字
     */
    String USER_NAME = "userName";

}
