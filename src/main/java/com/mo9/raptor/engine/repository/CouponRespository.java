package com.mo9.raptor.engine.repository;

import com.mo9.raptor.engine.entity.CouponEntity;
import com.mo9.raptor.entity.CashAccountLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by xzhang on 2018/9/28.
 */
public interface CouponRespository extends JpaRepository<CouponEntity,Long> , JpaSpecificationExecutor<CouponEntity> {

    @Query(value = "select * from t_raptor_coupon where bound_order_id = ?1 and status = 'BUNDLED' and effective_date <= ?2 AND expire_date > ?2 and deleted = false ", nativeQuery = true)
    CouponEntity getBundledCoupon(String loanOrderId, Long now);


    /**
     * 优惠券号获取优惠券
     * @param couponId
     * @return
     */
    @Query(value = "select * from t_raptor_coupon where coupon_id = ?1 and deleted = false", nativeQuery = true)
    CouponEntity getByCouponId(String couponId);

    /**
     * 获得此订单所有的减免金额
     * @param orderId
     * @return
     */
    @Query(value = "select sum(entry_amount) as 'totalEntryAmount' from t_raptor_coupon where bound_order_id = ?1 and status = 'ENTRY_DONE' and deleted = false", nativeQuery = true)
    Map<String , BigDecimal> getTotalDeductedAmount(String orderId);

    /**
     * 根据还款订单号查询优惠券
     * @param payOrderId
     * @return
     */
    @Query(value = "select * from t_raptor_coupon where pay_order_id = ?1 and deleted = false", nativeQuery = true)
    List<CouponEntity> getByPayOrderId(String payOrderId);

    /**
     * 根据用户标识查询未删除优惠券
     * @param userCode
     * @return
     */
    @Query(value = "select * from t_raptor_coupon where user_code = ?1 and deleted = false", nativeQuery = true)
    List<CouponEntity> findByUserCodeNotDelete(String userCode);
}
