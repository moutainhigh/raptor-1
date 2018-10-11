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
    public RiskMergencyContact saveOrUpdate(RiskMergencyContact entity) {
        RiskMergencyContact exists = riskMergencyContactRepository.findByMobileAndContractTel(entity.getMobile(), entity.getContractTel());
        if (exists != null){
            exists.setCallLength(entity.getCallLength());
            exists.setCallTimes(entity.getCallTimes());
            exists.setContactName(entity.getContactName());
            exists.setContactPriority(entity.getContactPriority());
            exists.setContactRelationship(entity.getContactRelationship());
            exists.setFanchaTelloc(entity.getFanchaTelloc());
            exists.setTagsFinancial(entity.getTagsFinancial());
            exists.setTagsLabel(entity.getTagsLabel());
            exists.setUpdateTime(System.currentTimeMillis());
            
            return riskMergencyContactRepository.save(exists);
        }
        
        return riskMergencyContactRepository.save(entity);
    }
}
