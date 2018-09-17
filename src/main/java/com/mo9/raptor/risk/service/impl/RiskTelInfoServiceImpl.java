package com.mo9.raptor.risk.service.impl;

import com.mo9.raptor.risk.entity.TRiskTelInfo;
import com.mo9.raptor.risk.repo.RiskTelInfoRepository;
import com.mo9.raptor.risk.service.RiskTelInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 14:54 .
 */

@Service("riskTelInfoService")
public class RiskTelInfoServiceImpl implements RiskTelInfoService {
    
    @Resource
    private RiskTelInfoRepository riskTelInfoRepository;
    
    @Override
    public void save(TRiskTelInfo riskTelInfo) {
        riskTelInfoRepository.save(riskTelInfo);        
    }
}
