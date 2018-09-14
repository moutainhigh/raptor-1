package com.mo9.raptor.engine.service.impl;

import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.service.ILoanOrderService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xzhang on 2018/9/14.
 */
@Service("loanOrderServiceImpl")
public class LoanOrderServiceImpl implements ILoanOrderService {
    @Override
    public List<LoanOrderEntity> listUserLentOrders(String userCode) {
        return null;
    }

    @Override
    public List<LoanOrderEntity> listByOrderIds(List<String> orderIds) {
        return null;
    }

    @Override
    public List<LoanOrderEntity> listUserOrderByStatuses(String userCode, List<String> statuses) {
        return null;
    }

    @Override
    public LoanOrderEntity getByOrderId(String orderId) {
        return null;
    }

    @Override
    public LoanOrderEntity save(LoanOrderEntity loanOrder) {
        return null;
    }
}
