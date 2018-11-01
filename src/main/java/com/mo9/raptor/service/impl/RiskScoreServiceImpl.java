package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.RiskScoreEntity;
import com.mo9.raptor.repository.RiskScoreRepository;
import com.mo9.raptor.service.RiskScoreService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RiskScoreServiceImpl implements RiskScoreService {


    @Resource
    private RiskScoreRepository riskScoreRepository;

    @Override
    public RiskScoreEntity create(String userCode, String mobile, Double score, String version,String result) {
        RiskScoreEntity riskScoreEntity = new RiskScoreEntity();
        riskScoreEntity.setMobile(mobile);
        riskScoreEntity.setUserCode(userCode);
        riskScoreEntity.setScore(score);
        riskScoreEntity.setVersion(version);
        riskScoreEntity.setResult(result);
        riskScoreEntity.setCreateTime(System.currentTimeMillis());
        return riskScoreRepository.save(riskScoreEntity);
    }
}
