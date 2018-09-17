package com.mo9.raptor.risk.service.impl;

import com.mo9.raptor.bean.req.risk.CallLogReq;
import com.mo9.raptor.risk.entity.TRiskTelInfo;
import com.mo9.raptor.risk.repo.RiskTelInfoRepository;
import com.mo9.raptor.risk.service.RiskTelInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public TRiskTelInfo save(TRiskTelInfo riskTelInfo) {
        return riskTelInfoRepository.save(riskTelInfo);        
    }

    @Override
    public TRiskTelInfo coverReq2Entity(CallLogReq callLogReq) {
        TRiskTelInfo riskTelInfo = new TRiskTelInfo();
        riskTelInfo.setMobile(callLogReq.getData().getTel());
        riskTelInfo.setAddress(callLogReq.getData().getTel_info().getAddress());
        riskTelInfo.setFullName(callLogReq.getData().getTel_info().getFull_name());
        riskTelInfo.setIdCard(callLogReq.getData().getTel_info().getId_card());
        
        return riskTelInfo;
    }
}
