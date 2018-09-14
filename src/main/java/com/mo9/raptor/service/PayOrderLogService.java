package com.mo9.raptor.service;

import com.mo9.raptor.entity.PayOrderLogEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 还款订单service
 * Created by xzhang on 2018/9/13.
 */
public interface PayOrderLogService {

    @Transactional(rollbackFor = Exception.class)
    void save(PayOrderLogEntity payOrderLog);


    /**
     * 根据借款订单号获取
     * @param orderId  借款订单号
     * @return         logs
     */
    List<PayOrderLogEntity> listByOrderId(String orderId);

    /**
     * 根据订单号获取订单
     * @param payOrderId  还款订单号
     * @return            还款订单
     */
    PayOrderLogEntity getByPayOrderId(String payOrderId);



}
