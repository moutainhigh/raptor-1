package com.mo9.raptor.engine.service.impl;

import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.repository.PayOrderRepository;
import com.mo9.raptor.engine.service.IPayOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by xzhang on 2018/7/8.
 */
@Service("payOrderService")
public class PayOrderServiceImpl implements IPayOrderService {

    private static final Logger logger = LoggerFactory.getLogger(PayOrderServiceImpl.class);

    @Autowired
    private PayOrderRepository payOrderRepository;

    @Override
    public PayOrderEntity getByOrderId(String orderId) {
        return payOrderRepository.getByOrderId(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayOrderEntity save(PayOrderEntity loanOrder) {
         return payOrderRepository.save(loanOrder);
    }
}
