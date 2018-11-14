package com.mo9.raptor.engine.service.impl;

import com.mo9.raptor.engine.entity.CouponEntity;
import com.mo9.raptor.engine.repository.CouponRespository;
import com.mo9.raptor.engine.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 优惠券service
 * Created by xzhang on 2018/9/28.
 */
@Service("couponServiceImpl")
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponRespository couponRespository;

    @Override
    public CouponEntity getByCouponId(String couponId) {
        return couponRespository.getByCouponId(couponId);
    }

    @Override
    public BigDecimal getTotalDeductedAmount(String orderId) {
        Map<String, BigDecimal> totalDeductedAmount = couponRespository.getTotalDeductedAmount(orderId);
        return totalDeductedAmount.get("totalEntryAmount");
    }

    @Override
    public List<CouponEntity> getByPayOrderId(String payOrderId) {
        return couponRespository.getByPayOrderId(payOrderId);
    }

    @Override
    public CouponEntity getEffectiveBundledCoupon(String loanOrderId) {
        return couponRespository.getBundledCoupon(loanOrderId, System.currentTimeMillis());
    }

    @Override
    public CouponEntity save(CouponEntity couponEntity) {
        return couponRespository.save(couponEntity);
    }
}
