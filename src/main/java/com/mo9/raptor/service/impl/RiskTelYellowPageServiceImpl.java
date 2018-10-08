package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.RiskTelYellowPage;
import com.mo9.raptor.repository.RiskTelYellowPageRepository;
import com.mo9.raptor.service.RiskTelYellowPageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wtwei .
 * @date 2018/10/8 .
 * @time 14:46 .
 */

@Service("riskTelYellowPageService")
public class RiskTelYellowPageServiceImpl implements RiskTelYellowPageService {
    @Resource
    private RiskTelYellowPageRepository riskTelYellowPageRepository;
    
    @Override
    public RiskTelYellowPage save(RiskTelYellowPage entity) {
        return riskTelYellowPageRepository.save(entity);
    }

    @Override
    public RiskTelYellowPage update(RiskTelYellowPage updateEntity) {
        return riskTelYellowPageRepository.saveAndFlush(updateEntity);
    }
}