package com.mo9.raptor.engine.service.impl;

import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.repository.LoanOrderRepository;
import com.mo9.raptor.engine.service.ILoanOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by xzhang on 2018/7/8.
 */
@Service("loanOrderService")
public class LoanOrderServiceImpl implements ILoanOrderService {

    private static final Logger logger = LoggerFactory.getLogger(LoanOrderServiceImpl.class);

    @Autowired
    LoanOrderRepository loanOrderRepository;

    @Override
    public LoanOrderEntity getByOrderId(String orderId) {
        return loanOrderRepository.getByOrderId(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoanOrderEntity save(LoanOrderEntity loanOrder) {
         return loanOrderRepository.save(loanOrder);
    }
}
