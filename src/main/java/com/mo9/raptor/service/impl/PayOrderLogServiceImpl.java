package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.PayOrderLogEntity;
import com.mo9.raptor.repository.PayOrderLogRepository;
import com.mo9.raptor.service.PayOrderLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by xzhang on 2018/9/13.
 */
@Service("payOrderLogServiceImpl")
public class PayOrderLogServiceImpl implements PayOrderLogService {

    @Autowired
    private PayOrderLogRepository payOrderLogRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(PayOrderLogEntity payOrderLog) {
        payOrderLogRepository.save(payOrderLog);
    }

    @Override
    public List<PayOrderLogEntity> listByOrderId(String orderId) {
        return payOrderLogRepository.listByOrderId(orderId);
    }

    @Override
    public PayOrderLogEntity getByPayOrderId(String payOrderId) {
        return payOrderLogRepository.getByPayOrderId(payOrderId);
    }


}
