package com.mo9.raptor.risk.service.impl;

import com.mo9.raptor.risk.entity.TRiskCallLog;
import com.mo9.raptor.risk.repo.RiskCallLogRepository;
import com.mo9.raptor.risk.service.RiskCallLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 13:45 .
 */
@Service("riskCallLogService")
public class RiskCallLogServiceImpl implements RiskCallLogService {
    
    @Resource
    private RiskCallLogRepository riskCallLogRepository;
    
    @Override
    public void save(TRiskCallLog riskCallLog) {
        riskCallLogRepository.save(riskCallLog);
    }
}
