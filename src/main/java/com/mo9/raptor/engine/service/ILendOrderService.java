package com.mo9.raptor.engine.service;

import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.enums.PayTypeEnum;

import java.util.List;

/**
 * 贷款订单service
 * Created by xzhang on 2018/7/6.
 */
public interface ILendOrderService {

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
    LendOrderEntity getByOrderId(String orderId);

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
}
