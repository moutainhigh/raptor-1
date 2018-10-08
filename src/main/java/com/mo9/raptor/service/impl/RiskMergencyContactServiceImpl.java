package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.RiskMergencyContact;
import com.mo9.raptor.repository.RiskMergencyContactRepository;
import com.mo9.raptor.service.RiskMergencyContactService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wtwei .
 * @date 2018/10/8 .
 * @time 14:54 .
 */

@Service("RiskMergencyContactService")
public class RiskMergencyContactServiceImpl implements RiskMergencyContactService {
    @Resource
    private RiskMergencyContactRepository riskMergencyContactRepository;
    
    @Override
    public RiskMergencyContact save(RiskMergencyContact entity) {
        return riskMergencyContactRepository.save(entity);
    }
}
