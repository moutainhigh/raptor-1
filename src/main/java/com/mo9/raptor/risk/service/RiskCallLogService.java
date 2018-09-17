package com.mo9.raptor.risk.service;

import com.mo9.raptor.bean.req.risk.CallLogReq;
import com.mo9.raptor.risk.entity.TRiskCallLog;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 13:43 .
 */

public interface RiskCallLogService {

    TRiskCallLog save(TRiskCallLog riskCallLog);

    void batchSave(List<TRiskCallLog> callLogList);

    List<TRiskCallLog> coverReqToEntity(CallLogReq callLogReq);
}
