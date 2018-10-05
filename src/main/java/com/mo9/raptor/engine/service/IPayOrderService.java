package com.mo9.raptor.engine.service;

import com.mo9.raptor.bean.condition.FetchPayOrderCondition;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.entity.PayOrderLogEntity;
import com.mo9.raptor.enums.PayTypeEnum;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 贷款订单service
 * Created by xzhang on 2018/7/6.
 */
public interface IPayOrderService {

    /** 获取已入账的借贷还款 */
    List<PayOrderEntity> listEntryDonePayLoan(String loanOrderId, List<String> types) ;


    /**
     * 根据订单号查询订单
     * @param orderId   还款订单号
     * @return          还款订单
     */
    PayOrderEntity getByOrderId(String orderId);

    /**
     * 根据订单号批量查询
     * @param payOrderIds
     * @return
     */
    List<PayOrderEntity> listByOrderIds(List<String> payOrderIds);

    /**
     * 根据借款订单和类型获取
     * @param loanOrderId
     * @param payType
     * @return
     */
    List<PayOrderEntity> listByLoanOrderIdAndType(String loanOrderId, PayTypeEnum payType);

    /**
     * 保存订单
     */
    void save(PayOrderEntity payOrder);

    /**
     * 所有还款订单查询
     * @return         还款订单
     */
    List<PayOrderEntity> listByUser(String userCode);

    /**
     * 创建还款订单
     * @param userCode 还款订单号
     * @param statuses 还款订单状态
     * @return         还款订单
     */
    List<PayOrderEntity> listByUserAndStatus(String userCode, List<String> statuses);

    /**
     * 根据条件查询借款订单
     * @param condition
     * @return
     */
    Page<PayOrderEntity> listPayOrderByCondition(FetchPayOrderCondition condition);

    /**
     * 用户还款, 通知先玩后付
     * @param payOrder
     */
    void savePayOrderAndLogAndNotice(PayOrderEntity payOrder, PayOrderLogEntity payOrderLog);

    /**
     * 用户还款
     * @param payOrder
     */
    void savePayOrderAndLog(PayOrderEntity payOrder, PayOrderLogEntity payOrderLog);

    /**
     * 发送扣款通知
     * @param payOrderId
     */
    void repayNotice(String payOrderId);

    /**
     * 根据状态查询
     * @param deducting
     * @return
     */
    List<PayOrderEntity> findByStatus(String deducting);
}
