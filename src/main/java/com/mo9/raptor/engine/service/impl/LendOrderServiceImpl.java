package com.mo9.raptor.engine.service.impl;

import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.repository.LendOrderRepository;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by xzhang on 2018/7/8.
 */
@Service("lendOrderService")
public class LendOrderServiceImpl implements ILendOrderService {

    private static final Logger logger = LoggerFactory.getLogger(LendOrderServiceImpl.class);

    @Autowired
    private LendOrderRepository lendOrderRepository;

    @Override
    public LendOrderEntity getByOrderId(String orderId) {
        return lendOrderRepository.getByOrderId(orderId);
    }

    @Override
    public LendOrderEntity save(LendOrderEntity loanOrder) {
         return lendOrderRepository.save(loanOrder);
    }

    @Override
    public BigDecimal getDailyLendAmount() {
        Long date = TimeUtils.extractDateTime(System.currentTimeMillis());
        Map<String, BigDecimal> dailyLendAmount = lendOrderRepository.getTotalLendAmount(date);
        BigDecimal lendAmount = dailyLendAmount.get("dailyLendAmount");
        return lendAmount == null ? BigDecimal.ZERO : lendAmount;
    }
}
