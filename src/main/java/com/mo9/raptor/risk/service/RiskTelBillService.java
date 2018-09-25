package com.mo9.raptor.risk.service;

import com.mo9.raptor.bean.req.risk.CallLogReq;
import com.mo9.raptor.risk.entity.TRiskTelBill;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 14:56 .
 */


public interface RiskTelBillService {
    TRiskTelBill save(TRiskTelBill riskTelBill);

    @Transactional(rollbackFor = Exception.class)
    void saveAll(List<TRiskTelBill> riskTelBillList);

    void batchSave(List<TRiskTelBill> riskTelBillList);

    List<TRiskTelBill> coverReq2Entity(CallLogReq callLogReq);
}
