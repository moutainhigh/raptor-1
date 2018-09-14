package com.mo9.raptor.repository;

import com.mo9.raptor.entity.PayOrderLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by xzhang on 2018/9/12.
 */
public interface PayOrderLogRepository extends JpaRepository<PayOrderLogEntity,Long>, JpaSpecificationExecutor<PayOrderLogEntity> {

    /**
     * 根据借款订单号获取
     * @param orderId  借款订单号
     * @return         logs
     */
    @Query(value = "select * from t_raptor_pay_order_log where order_id = ?1 and deleted = false", nativeQuery = true)
    List<PayOrderLogEntity> listByOrderId(String orderId);

    /**
     * 根据订单号获取订单
     * @param payOrderId  还款订单号
     * @return            还款订单
     */
    @Query(value = "select * from t_raptor_pay_order_log where pay_order_id = ?1 and deleted = false", nativeQuery = true)
    PayOrderLogEntity getByPayOrderId(String payOrderId);
}
