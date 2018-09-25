package com.mo9.raptor.risk.service.impl;

import com.mo9.raptor.bean.req.risk.CallLogReq;
import com.mo9.raptor.risk.entity.TRiskCallLog;
import com.mo9.raptor.risk.entity.TRiskTelBill;
import com.mo9.raptor.risk.entity.TRiskTelInfo;
import com.mo9.raptor.risk.repo.RiskTelInfoRepository;
import com.mo9.raptor.risk.service.RiskCallLogService;
import com.mo9.raptor.risk.service.RiskTelBillService;
import com.mo9.raptor.risk.service.RiskTelInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 14:54 .
 */

@Service("riskTelInfoService")
public class RiskTelInfoServiceImpl implements RiskTelInfoService {
    
    @Resource
    private RiskTelInfoRepository riskTelInfoRepository;

    @Resource
    private RiskTelBillService riskTelBillService;

    @Resource
    private RiskCallLogService riskCallLogService;
    
    @Override
    public TRiskTelInfo save(TRiskTelInfo riskTelInfo) {
        TRiskTelInfo exists = riskTelInfoRepository.findByMobile(riskTelInfo.getMobile());
        if(exists != null){
            return exists;
        }
        return riskTelInfoRepository.save(riskTelInfo);        
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAllCallLogData(CallLogReq callLogReq){
        //机主信息
        TRiskTelInfo riskTelInfo = this.coverReq2Entity(callLogReq);
        this.save(riskTelInfo);

        //账单信息
        List<TRiskTelBill> riskTelBillList = riskTelBillService.coverReq2Entity(callLogReq);
        riskTelBillService.batchSave(riskTelBillList);

        //通话记录
        List<TRiskCallLog> riskCallLogList = riskCallLogService.coverReqToEntity(callLogReq);
        riskCallLogService.batchSave(riskCallLogList);
    }

    @Override
    public TRiskTelInfo coverReq2Entity(CallLogReq callLogReq) {
        TRiskTelInfo riskTelInfo = new TRiskTelInfo();

        riskTelInfo.setSid(callLogReq.getData().getSid());
        riskTelInfo.setMobile(callLogReq.getData().getTel());
        riskTelInfo.setUid(callLogReq.getData().getUid());

        if(callLogReq.getData().getTel_info() != null){
            riskTelInfo.setOpenDate(callLogReq.getData().getTel_info().getOpen_date());
            riskTelInfo.setAddress(callLogReq.getData().getTel_info().getAddress());
            riskTelInfo.setFullName(callLogReq.getData().getTel_info().getFull_name());
            riskTelInfo.setIdCard(callLogReq.getData().getTel_info().getId_card());
        }
        
        return riskTelInfo;
    }
}
