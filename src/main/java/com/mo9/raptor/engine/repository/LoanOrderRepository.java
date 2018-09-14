package com.mo9.raptor.engine.repository;

import com.mo9.raptor.engine.entity.LoanOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by xzhang on 2018/7/8.
 */
public interface LoanOrderRepository extends JpaRepository<LoanOrderEntity,Long>, JpaSpecificationExecutor<LoanOrderEntity> {

    /**
     * 根据订单号获取订单
     * @param orderId  订单号
     * @return         订单
     */
    @Query(value = "select * from t_libra_loan_order where order_id=?1 and deleted = false", nativeQuery = true)
    LoanOrderEntity getByOrderId(String orderId);

}
