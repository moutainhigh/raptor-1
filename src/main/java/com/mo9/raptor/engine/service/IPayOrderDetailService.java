package com.mo9.raptor.engine.service;

import com.mo9.raptor.bean.res.RepayDetailRes;
import com.mo9.raptor.engine.entity.PayOrderDetailEntity;

import java.util.List;

/**
 * Created by xzhang on 2018/9/17.
 */
public interface IPayOrderDetailService {

    /**
     * 根据订单号获取订单
     * @param orderId  订单号
     * @return         明细
     */
    List<PayOrderDetailEntity> listByOrderId(String orderId);

    /**
     * 根据还款订单号获取订单
     * @param payOrderId  还款订单号
     * @return         明细
     */
    List<PayOrderDetailEntity> listByPayOrderId(String payOrderId);

    /**
     * 保存订单
     */
    List<PayOrderDetailEntity> saveItem (List<PayOrderDetailEntity> payOrderDetails);

    /**
     * 获得还款明细
     * @param orderId
     * @return
     */
    List<RepayDetailRes> getRepayDetail(String orderId);
}
