package com.mo9.raptor.engine.service;

import com.mo9.raptor.bean.condition.FetchLoanOrderCondition;
import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 贷款订单service
 * Created by xzhang on 2018/7/6.
 */
public interface ILoanOrderService {

    /**
     * 根据订单号获取
     * 会刷新所有预下单订单
     * @param orderId  订单号
     * @return         订单
     */
    LoanOrderEntity getByOrderId(String orderId);

    /**
     * 保存订单
     */
    LoanOrderEntity save(LoanOrderEntity loanOrder);

    /**
     * 分页查询订单
     * @param condition
     * @return
     */
    Page<LoanOrderEntity> listLoanOrderByCondition(FetchLoanOrderCondition condition);

    /**
     * 获取上一条为完成的订单
     * @param userCode
     * @return
     */
    LoanOrderEntity getLastIncompleteOrder(String userCode);

    /**
     * 获取上一条为完成的订单
     * @param userCode
     * @param processing
     * @return
     */
    LoanOrderEntity getLastIncompleteOrder(String userCode, List<String> processing);


    /**
     * 根据还款日获取订单
     * @param begin
     * @param end
     * @return
     */
    List<LoanOrderEntity> listByRepaymentDate(Long begin, Long end);

    /**
     * 创建放款订单及借款订单
     * @param loanOrder
     * @param lendOrder
     */
    void saveLendOrder(LoanOrderEntity loanOrder, LendOrderEntity lendOrder) throws Exception;

    /**
     * 根据状态查询订单
     * @param statusEnums
     * @return
     */
    List<LoanOrderEntity> listByStatus(List<String> statusEnums);

    /**
     * 查询所有今天还款的订单
     * @return
     */
    List<LoanOrderEntity> listShouldPayOrder();
}
