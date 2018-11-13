package com.mo9.raptor.engine.service;

import com.mo9.raptor.bean.condition.CouponCondition;
import com.mo9.raptor.engine.entity.CouponEntity;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
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

    /**
     * 获得此订单所有的减免金额
     * @param orderId
     * @return
     */
    BigDecimal getTotalDeductedAmount(String orderId);

    /**
     * 根据还款订单号查询优惠券
     * @param payOrderId
     * @return
     */
    List<CouponEntity> getByPayOrderId(String payOrderId);

    /**
     * 根据用户标识查询未删除优惠券
     * @param userCode
     * @return
     */
    List<CouponEntity> findByUserCodeNotDelete(String userCode);

    /**
     * 根据条件查询分页
     * @param couponCondition
     * @return
     */
    Page<CouponEntity> findByCondition(CouponCondition couponCondition);

}
