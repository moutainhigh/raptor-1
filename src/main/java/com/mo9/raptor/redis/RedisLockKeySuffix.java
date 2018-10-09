package com.mo9.raptor.redis;

/**
 * redis锁使用的key的后缀, 保证在不该出现一样的key的地方不出现一样的key
 * Created by xzhang on 2018/7/9.
 */
public interface RedisLockKeySuffix {

    /**
     * 订单创建/绑定优惠券行为锁
     */
    String LOAN_COUPON_CREATE_KEY = "LOAN_COUPON_CREATE_KEY";

    /**
     * 订单预下单锁
     */
    String PRE_LOAN_ORDER_KEY = "PRE_LOAN_ORDER_KEY";

    /**
     * 下单锁
     */
    String LOAN_ORDER_KEY = "LOAN_ORDER_KEY";

    /**
     * 预还款锁
     */
    String PRE_PAY_ORDER_KEY = "PRE_PAY_ORDER_KEY";

    /**
     * 还款锁
     */
    String PAY_ORDER_KEY = "PAY_ORDER_KEY";

}
