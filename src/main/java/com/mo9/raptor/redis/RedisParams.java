package com.mo9.raptor.redis;

/**
 * @author jyou
 *
 * 定义一些redis需要的参数
 */
public class RedisParams {
    /**
     * 10秒
     */
    public static final long EXPIRE_10S = 10;

    /**
     * 30秒
     */
    public static final long EXPIRE_30S = 30;

    /**
     * 1分钟,xmemcached对应的是秒
     */

    public static final long EXPIRE_1M = 60;

    /**
     * 5分钟,xmemcached对应的是秒
     */
    public static final long EXPIRE_5M = 5 * 60;

    /**
     * 10分钟,xmemcached对应的是秒
     */
    public static final long EXPIRE_10M = 10 * 60;

    /**
     * 30分钟,xmemcached对应的是秒
     */
    public static final long EXPIRE_30M = 30 * 60;

    /**
     * 1天,xmemcached对应的是秒
     */
    public static final long EXPIRE_1D = 60 * 60 *24;
    /**
     * 15天,xmemcached对应的是秒
     */
    public static final long EXPIRE_15D = 60 * 60 *24  * 15;
    /**
     * 14天,xmemcached对应的是秒
     */
    public static final long EXPIRE_14D = 60 * 60 *24  * 14;

    /**
     * ip限制
     */
    public static final String LIMIT_IP_RATES = "limit_ip_rates_";

    /**
     * 存储token
     */
    public static final String ACCESS_TOKEN_KEY = "access_token_key_";

    /**
     * 限制获取验证码频率
     */
    public static final String LIMIT_CAPTCHA_KEY = "limit_captcha_key";
    /**
     * 图形验证码
     */
    public static final String GRAPHIC_CAPTCHA_KEY = "graphic_captcha_key";

    /**
     * 手机验证码key
     */
    public static final String MOBILE_CAPTCHA_KEY = "mobile_captcha_key_";

    /**
     * 邮箱验证码key
     */
    public static final String EMAIL_CAPTCHA_KEY = "email_captcha_key_";

    /**
     * 存储策略的key
     */
    public static final String STRATEGY_KEY = "strategy_key_";

    /**
     * 行为验证的key
     */
    public static final String CAPTCHA_KEY = "captcha_key_";

    /**
     * 第三方认证成功向redis压栈用户userCode的专用key
     */
    public static final String AUTH_SUCCESS_KEY = "auth_success_notice_risk_key";

    /**
     * 用于保存临时token的key
     */
    public static final String ACTION_TOKEN_LONG = "action_token_long_";

    /**
     * 修改资金密码限制错误次数key
     */
    public static final String UPDATE_TRADE_PWD_LIMIT_TIMES = "update_trade_pwd_limit_times_";

    /**
     * 检查验证码校验频率key
     */
    public static final String CHECK_CAPTCHA_LIMIT_TIMES = "check_captcha_limit_times_";

    /**
     * 检查校验交易密码错误次数
     */
    public static final String CHECK_TRADE_LIMIT_TIMES = "check_trade_limit_times_";

    /**
     * lba借贷订单频率限制
     */
    public static final String LIMIT_LOAN_LBA_RATES = "limit_loan_lba_rates_";

    public static String getActionToken(String actionToken){
        return ACTION_TOKEN_LONG + actionToken;
    }
    public static String getAccessToken(String clientId ,String accessCode){
        return ACCESS_TOKEN_KEY + clientId + accessCode;
    }
    public static String getUpdateTradePwdLimit(String userCode){
        return UPDATE_TRADE_PWD_LIMIT_TIMES + userCode;
    }

    public static String getCheckTradePwdLimit(String userCode){
        return CHECK_TRADE_LIMIT_TIMES + userCode;
    }
}
