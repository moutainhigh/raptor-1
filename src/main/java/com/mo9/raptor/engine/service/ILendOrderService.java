package com.mo9.raptor.engine.service;

import com.mo9.raptor.engine.entity.LendOrderEntity;

/**
 * 贷款订单service
 * Created by xzhang on 2018/7/6.
 */
public interface ILendOrderService {

    /**
     * 根据订单号获取
     * 会刷新所有预下单订单
     * @param orderId  订单号
     * @return         订单
     */
    LendOrderEntity getByOrderId(String orderId);

    /**
     * 保存订单
     */
    LendOrderEntity save(LendOrderEntity loanOrder);

}
