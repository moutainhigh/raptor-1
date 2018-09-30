package com.mo9.raptor.engine.service;

import com.mo9.raptor.engine.entity.CouponEntity;

import java.util.List;

/**
 * 优惠券service
 * Created by xzhang on 2018/9/28.
 */
public interface CouponService {

    CouponEntity save (CouponEntity couponEntity);

    /** 获取已绑定订单的有效的优惠券 */
    CouponEntity getEffectiveBundledCoupon (String loanOrderId);
    /**
     * 优惠券号获取优惠券
     * @param couponId
     * @return
     */
    CouponEntity getByCouponId(String couponId);

}
