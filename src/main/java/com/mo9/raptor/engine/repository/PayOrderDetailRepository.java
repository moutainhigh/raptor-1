package com.mo9.raptor.engine.repository;

import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by xzhang on 2018/7/8.
 */
public interface PayOrderDetailRepository extends JpaRepository<PayOrderDetailEntity,Long> {

    /**
     * 根据订单号获取订单
     * @param orderId  订单号
     * @return         明细
     */
    @Query(value = "select * from t_raptor_pay_order_detail where destination_id=?1 and deleted = false", nativeQuery = true)
    List<PayOrderDetailEntity> listByOrderId(String orderId);

    /**
     * 根据还款订单号获取订单
     * @param payOrderId  还款订单号
     * @return         明细
     */
    @Query(value = "select * from t_raptor_pay_order_detail where source_id=?1 and deleted = false", nativeQuery = true)
    List<PayOrderDetailEntity> listByPayOrderId(String payOrderId);

}
