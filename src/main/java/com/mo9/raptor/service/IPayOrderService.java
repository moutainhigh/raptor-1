package com.mo9.raptor.service;

import com.mo9.raptor.bean.condition.FetchPayOrderCondition;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 贷款订单service
 * Created by xzhang on 2018/7/6.
 */
public interface IPayOrderService {

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
    void repay(PayOrderEntity payOrder);
}
