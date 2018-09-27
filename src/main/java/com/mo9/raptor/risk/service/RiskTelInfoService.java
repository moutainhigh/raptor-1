package com.mo9.raptor.risk.service;

import com.mo9.raptor.bean.req.risk.CallLogReq;
import com.mo9.raptor.risk.entity.TRiskTelInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 14:53 .
 */

public interface RiskTelInfoService {

    TRiskTelInfo findByMobile(String mobile);
    
    TRiskTelInfo save(TRiskTelInfo riskTelInfo);

    void saveAllCallLogData(CallLogReq callLogReq);

    TRiskTelInfo coverReq2Entity(CallLogReq callLogReq);
    
    List<TRiskTelInfo> findNoReportTelInfo(Date start);
    
    TRiskTelInfo update(TRiskTelInfo riskTelInfo);
}
