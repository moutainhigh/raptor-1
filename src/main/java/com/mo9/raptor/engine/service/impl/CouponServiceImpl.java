package com.mo9.raptor.engine.service.impl;

import com.mo9.raptor.engine.entity.CouponEntity;
import com.mo9.raptor.engine.repository.CouponRespository;
import com.mo9.raptor.engine.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 优惠券service
 * Created by xzhang on 2018/9/28.
 */
@Service("couponServiceImpl")
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponRespository couponRespository;

    @Override
    public CouponEntity getUserUnboundCoupon(String userCode) {
        return couponRespository.getUserUnboundCoupon(userCode);
    }

    @Override
    public CouponEntity getByCouponId(String couponId) {
        return couponRespository.getByCouponId(couponId);
    }

    @Override
    public CouponEntity getEffectiveByLoanOrderId(String loanOrderId) {
        //TODO:
        return null;
    }
}
