package com.mo9.raptor.risk.service.impl;

import com.mo9.raptor.bean.req.risk.Bill;
import com.mo9.raptor.bean.req.risk.CallLogReq;
import com.mo9.raptor.risk.entity.TRiskTelBill;
import com.mo9.raptor.risk.repo.RiskTelBillRepository;
import com.mo9.raptor.risk.service.RiskTelBillService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 14:57 .
 */

@Service("riskTelBillService")
public class RiskTelBillServiceImpl implements RiskTelBillService {
    
    @Resource
    private RiskTelBillRepository riskTelBillRepository;
    
    @Override
    public TRiskTelBill save(TRiskTelBill riskTelBill) {
        return riskTelBillRepository.save(riskTelBill);
    }

    @Override
    public void batchSave(List<TRiskTelBill> riskTelBillList) {
        riskTelBillRepository.saveAll(riskTelBillList);
    }

    @Override
    public List<TRiskTelBill> coverReq2Entity(CallLogReq callLogReq) {
        List<TRiskTelBill> riskTelBillList = new ArrayList<>();
        List<Bill> callLogBill = callLogReq.getData().getBill();

        TRiskTelBill riskTelBill = null;
        for (Bill bill : callLogBill) {
            riskTelBill = new TRiskTelBill();

            riskTelBill.setSid(callLogReq.getData().getSid());
            riskTelBill.setUid(callLogReq.getData().getUid());
            riskTelBill.setMobile(callLogReq.getData().getTel());
            
            riskTelBill.setBillAmount(bill.getBill_amount());
            riskTelBill.setBillZengzhifei(bill.getBill_zengzhifei());
            riskTelBill.setBillQita(bill.getBill_qita());
            riskTelBill.setBillPackage(bill.getBill_package());
            riskTelBill.setBillExtSms(bill.getBill_ext_sms());
            riskTelBill.setBillDaishoufei(bill.getBill_daishoufei());
            riskTelBill.setBillMonth(bill.getBill_month());
            riskTelBill.setBillExtCalls(bill.getBill_ext_calls());

            riskTelBillList.add(riskTelBill);
        }
        return riskTelBillList;
    }
}
