package com.mo9.raptor.risk.service.impl;

import com.mo9.raptor.risk.entity.TRiskTelBill;
import com.mo9.raptor.risk.repo.RiskTelBillRepository;
import com.mo9.raptor.risk.service.RiskTelBillService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 14:57 .
 */

@Service("riskTelBillService")
public class RiskTelBillServiceImpl implements RiskTelBillService {
    
    @Resource
    private RiskTelBillRepository riskTelBillRepository;
    
    @Override
    public void save(TRiskTelBill riskTelBill) {
        riskTelBillRepository.save(riskTelBill);
    }
}
