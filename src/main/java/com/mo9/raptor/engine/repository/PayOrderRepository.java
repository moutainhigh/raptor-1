package com.mo9.raptor.engine.repository;

import com.mo9.raptor.engine.entity.PayOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by xzhang on 2018/7/8.
 */
public interface PayOrderRepository extends JpaRepository<PayOrderEntity,Long>, JpaSpecificationExecutor<PayOrderEntity> {

    /**
     * 根据订单号获取订单
     * @param orderId  订单号
     * @return         订单
     */
    @Query(value = "select * from t_pay_loan_order where order_id=?1 and deleted = false", nativeQuery = true)
    PayOrderEntity getByOrderId(String orderId);

}
