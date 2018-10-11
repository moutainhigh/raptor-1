package com.mo9.raptor.service;

import com.mo9.raptor.entity.RiskScoreEntity;
import com.mo9.raptor.entity.RuleLogEntity;

public interface RiskScoreService {

    RiskScoreEntity create(String userCode, String mobile, Double score, String result);

}
