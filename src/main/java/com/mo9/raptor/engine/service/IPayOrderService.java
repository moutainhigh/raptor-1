package com.mo9.raptor.engine.service;

import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.enums.PayTypeEnum;

import java.util.List;

/**
 * 贷款订单service
 * Created by xzhang on 2018/7/6.
 */
public interface IPayOrderService {


    List<PayOrderEntity> listOrderEntryDoings(String loanOrderId);

    /**
     * 根据批次号查询订单
     * @param batchId
     * @return
     */
    List<PayOrderEntity> listByBatchId(String batchId);

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
     * 保存订单
     */
    void save(PayOrderEntity loanOrder);

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

    /** 入账成功后通知风控 */
    void notifyRepaySuccess(String userCode, String batchId, PayTypeEnum payType);

    /**
     * 查询入账中的订单, 包括 ENTRY_DOING, DEDUCTED
     * @return
     */
    List<PayOrderEntity> listEntryDoingPayOrders(String userCode);
}
