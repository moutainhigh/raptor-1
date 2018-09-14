package com.mo9.raptor.engine.service;

import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.enums.ResCodeEnum;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 贷款订单service
 * Created by xzhang on 2018/7/6.
 */
public interface ILoanOrderService {

    /**
     * 查询用户已放款订单
     * @param userCode  用户
     * @return  订单
     */
    List<LoanOrderEntity> listUserLentOrders(String userCode);

    /**
     * 根据订单号查订单
     * @param orderIds
     * @return
     */
    List<LoanOrderEntity> listByOrderIds(List<String> orderIds);

    /**
     * 根据用户和状态查询订单
     * @param userCode  用户
     * @param statuses  状态
     * @return  订单
     */
    List<LoanOrderEntity> listUserOrderByStatuses(String userCode, List<String> statuses);

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
}
