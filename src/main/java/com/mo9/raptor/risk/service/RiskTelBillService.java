package com.mo9.raptor.risk.service;

import com.mo9.raptor.bean.req.risk.CallLogReq;
import com.mo9.raptor.risk.entity.TRiskTelBill;

import java.util.List;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 14:56 .
 */


public interface RiskTelBillService {
    TRiskTelBill save(TRiskTelBill riskTelBill);
    
    void batchSave(List<TRiskTelBill> riskTelBillList);

    List<TRiskTelBill> coverReq2Entity(CallLogReq callLogReq);
}
