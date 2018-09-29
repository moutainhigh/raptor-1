package com.mo9.raptor.engine.repository;

import com.mo9.raptor.engine.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by xzhang on 2018/9/28.
 */
public interface CouponRespository extends JpaRepository<CouponEntity,Long> {

    /**
     * 获取用户最新的一张未绑定的订单
     * @param userCode
     * @return
     */
    @Query(value = "select * from t_raptor_coupon where owner_id = ?1 and deleted = false order by create_time DESC LIMIT 1", nativeQuery = true)
    CouponEntity getUserUnboundCoupon(String userCode);

    /**
     * 优惠券号获取优惠券
     * @param couponId
     * @return
     */
    @Query(value = "select * from t_raptor_coupon where coupon_id = ?1 and deleted = false", nativeQuery = true)
    CouponEntity getByCouponId(String couponId);
}
