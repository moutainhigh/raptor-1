package com.mo9.raptor.repository;

import com.mo9.raptor.engine.entity.PayOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by xzhang on 2018/9/12.
 */
public interface PayOrderRepository extends JpaRepository<PayOrderEntity,Long>, JpaSpecificationExecutor<PayOrderEntity> {

    /**
     * 根据订单号获取订单
     * @param orderId  还款订单号
     * @return         还款订单
     */
    @Query(value = "select * from t_raptor_pay_order where order_id = ?1 and deleted = false", nativeQuery = true)
    PayOrderEntity getByOrderId(String orderId);

    /**
     * 根据订单号获取订单
     * @param orderIds  还款订单号
     * @return         还款订单
     */
    @Query(value = "select * from t_raptor_pay_order where order_id in ?1 and deleted = false", nativeQuery = true)
    List<PayOrderEntity> listByOrderIds(List<String> orderIds);

    /**
     * 根据订单号获取订单
     * @param loanOrderId  借款订单号
     * @return         还款订单
     */
    @Query(value = "select * from t_raptor_pay_order where loan_order_id = ?1 and status in ?2 and deleted = false", nativeQuery = true)
    List<PayOrderEntity> listByLoanOrderIdAndStatus(String loanOrderId, List<String> statuses);

    /**
     * 根据订单号获取订单
     * @param userCode  用户
     * @param statuses  还款订单状态
     * @return          还款订单
     */
    @Query(value = "select * from t_raptor_pay_order where owner_id = ?1 and status in ?2 and deleted = false", nativeQuery = true)
    List<PayOrderEntity> listByUserAndStatus(String userCode, List<String> statuses);

    /** 获取用户所有订单 */
    @Query(value = "select * from t_raptor_pay_order where owner_id = ?1 and deleted = false", nativeQuery = true)
    List<PayOrderEntity> listByUser(String userCode);

    @Query(value = "select * from t_raptor_pay_order where loan_order_id = ?1 AND type = ?2 and deleted = false", nativeQuery = true)
    List<PayOrderEntity> listByLoanOrderIdAndType(String loanOrderId, String payType);
}
