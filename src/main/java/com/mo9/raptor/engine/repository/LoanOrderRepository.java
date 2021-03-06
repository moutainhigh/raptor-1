package com.mo9.raptor.engine.repository;

import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by xzhang on 2018/7/8.
 */
public interface LoanOrderRepository extends JpaRepository<LoanOrderEntity,Long>, JpaSpecificationExecutor<LoanOrderEntity> {

    /**
     * 根据订单号获取订单
     * @param orderId  订单号
     * @return         订单
     */
    @Query(value = "select * from t_raptor_loan_order where order_id=?1 and deleted = false", nativeQuery = true)
    LoanOrderEntity getByOrderId(String orderId);

    /**
     * 获取上一条为完成的订单
     * @param userCode
     * @param processing
     * @return
     */
    @Query(value = "select * from t_raptor_loan_order where owner_id = ?1 AND status IN ?2 and deleted = false ORDER BY create_time DESC limit 1", nativeQuery = true)
    LoanOrderEntity getLastIncompleteOrder(String userCode, List<String> processing);

    /**
     * 获取上一条为完成的订单
     * @param userCode
     * @return
     */
    @Query(value = "select * from t_raptor_loan_order where owner_id = ?1 and deleted = false ORDER BY create_time DESC limit 1", nativeQuery = true)
    LoanOrderEntity getLastIncompleteOrder(String userCode);

    /**
     * 根据还款日获取订单
     * @param begin
     * @param end
     * @return
     */
    @Query(value = "select * from t_raptor_loan_order where repayment_date > ?1 AND repayment_date < ?2 and status = 'LENT' and deleted = false", nativeQuery = true)
    List<LoanOrderEntity> listByRepaymentDate(Long begin, Long end);

    /**
     * 根据状态查询订单
     * @param statusEnums
     * @return
     */
    @Query(value = "select * from t_raptor_loan_order where status in (?1) and deleted = false", nativeQuery = true)
    List<LoanOrderEntity> listByStatus(List<String> statusEnums);

    /**
     * 根据用户和状态查询订单
     * @param statusEnums
     * @return
     */
    @Query(value = "select * from t_raptor_loan_order where owner_id = ?1 and status in (?2) and deleted = false", nativeQuery = true)
    List<LoanOrderEntity> listByUserAndStatus(String userCode, List<String> statusEnums);

    /**
     * 查询所有今天还款的订单
     * @param today    今天0点
     * @param tomorrow 明天0点
     * @return
     */
    @Query(value = "select * from t_raptor_loan_order where repayment_date >= ?1 and repayment_date < ?2 and deleted = false", nativeQuery = true)
    List<LoanOrderEntity> listShouldPayOrder(Long today, Long tomorrow);

    /**
     *
     * @param time
     * @return
     */
    @Query(value = "select * from t_raptor_loan_order where repayment_date <= ?1 and status = 'LENT' and deleted = false", nativeQuery = true)
    List<LoanOrderEntity> listByOverDueOrder(long time);
}
