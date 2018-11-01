package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.RuleLogEntity;
import com.mo9.raptor.repository.RuleLogRepository;
import com.mo9.raptor.service.RuleLogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RuleLogServiceImpl implements RuleLogService {


    @Resource
    private RuleLogRepository ruleLogRepository;

    @Override
    public RuleLogEntity create(String userCode, String ruleName, Boolean hit, Boolean call, String remark) {
        RuleLogEntity ruleLogEntity = buildEntity(userCode, ruleName, hit, call, remark);
        return ruleLogRepository.save(ruleLogEntity);
    }

    @Override
    public RuleLogEntity create(String userCode, String ruleName, Boolean hit,Boolean call,String remark, String version, String subRule) {
        RuleLogEntity ruleLogEntity = buildEntity(userCode, ruleName, hit, call, remark);
        ruleLogEntity.setVersion(version);
        ruleLogEntity.setSubRule(subRule);
        return ruleLogRepository.save(ruleLogEntity);
    }

    private RuleLogEntity buildEntity(String userCode, String ruleName, Boolean hit, Boolean call, String remark){
        RuleLogEntity ruleLogEntity = new RuleLogEntity();
        ruleLogEntity.setCall(call);
        ruleLogEntity.setHit(hit);
        ruleLogEntity.setRuleName(ruleName);
        ruleLogEntity.setRemark(remark);
        ruleLogEntity.setCreateTime(System.currentTimeMillis());
        ruleLogEntity.setUid(userCode);
        return ruleLogEntity;
    }
}
