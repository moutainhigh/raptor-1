package com.mo9.raptor.engine.repository;

import com.mo9.raptor.engine.entity.LendOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by xzhang on 2018/7/8.
 */
public interface LendOrderRepository extends JpaRepository<LendOrderEntity,Long>, JpaSpecificationExecutor<LendOrderEntity> {

    /**
     * 根据订单号获取订单
     * @param orderId  订单号
     * @return         订单
     */
    @Query(value = "select * from t_raptor_lend_order where apply_unique_code = ?1 and deleted = false", nativeQuery = true)
    LendOrderEntity getByOrderId(String orderId);

    /**
     * 获取当天已放款金额
     * @return
     */
    @Query(value = "select sum(apply_number) as 'dailyLendAmount' from t_raptor_lend_order where status in ('LENDING', 'PENDING') AND create_time >= ?1 and deleted = false", nativeQuery = true)
    Map<String , BigDecimal> getTotalLendAmount(Long date);

    /**
     * 获取所有放款中的订单
     * @return
     */
    @Query(value = "select * from t_raptor_lend_order where status = 'LENDING' AND  deleted = false", nativeQuery = true)
    List<LendOrderEntity> listAllLendingOrder();
}
