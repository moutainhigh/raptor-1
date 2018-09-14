package com.mo9.raptor.engine.repository;

import com.mo9.raptor.engine.entity.LendOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by xzhang on 2018/7/8.
 */
public interface LendOrderRepository extends JpaRepository<LendOrderEntity,Long>, JpaSpecificationExecutor<LendOrderEntity> {

    /**
     * 根据订单号获取订单
     * @param orderId  订单号
     * @return         订单
     */
    @Query(value = "select * from t_libra_lend_order where order_id=?1 and deleted = false", nativeQuery = true)
    LendOrderEntity getByOrderId(String orderId);

}
