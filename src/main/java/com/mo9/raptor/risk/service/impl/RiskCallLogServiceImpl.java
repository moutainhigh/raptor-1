package com.mo9.raptor.risk.service.impl;

import com.mo9.raptor.bean.req.risk.CallLog;
import com.mo9.raptor.bean.req.risk.CallLogReq;
import com.mo9.raptor.risk.entity.TRiskCallLog;
import com.mo9.raptor.risk.repo.RiskCallLogRepository;
import com.mo9.raptor.risk.service.RiskCallLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 13:45 .
 */
@Service("riskCallLogService")
public class RiskCallLogServiceImpl implements RiskCallLogService {
    
    @Resource
    private RiskCallLogRepository riskCallLogRepository;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TRiskCallLog save(TRiskCallLog riskCallLog) {
        return riskCallLogRepository.save(riskCallLog);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAll(List<TRiskCallLog> callLogList){
        riskCallLogRepository.saveAll(callLogList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(List<TRiskCallLog> callLogList){
        for (TRiskCallLog callLog : callLogList) {
            TRiskCallLog exists = riskCallLogRepository.findOneCallLog(callLog.getMobile(), callLog.getCallTel(), callLog.getCallTime());
            if (exists != null){
                continue;
            }
            riskCallLogRepository.save(callLog);
        }
    }

    @Override
    public List<TRiskCallLog> coverReqToEntity(CallLogReq callLogReq){
        List<TRiskCallLog> callLogList = new ArrayList<>();
        List<CallLog> callLogs = callLogReq.getData().getCall_log();

        TRiskCallLog riskCallLog = null;
        for (CallLog callLog : callLogs) {
            riskCallLog = new TRiskCallLog();
            
            riskCallLog.setSid(callLogReq.getData().getSid());
            riskCallLog.setMobile(callLogReq.getData().getTel());
            riskCallLog.setUid(callLogReq.getData().getUid());

            if (callLog != null) {
                riskCallLog.setCallCost(callLog.getCall_cost());
                riskCallLog.setCallTime(callLog.getCall_time());
                riskCallLog.setCallMethod(callLog.getCall_method());
                riskCallLog.setCallType(callLog.getCall_type());
                riskCallLog.setCallTo(callLog.getCall_to());
                riskCallLog.setCallFrom(callLog.getCall_from());
                riskCallLog.setCallDuration(callLog.getCall_duration());
                riskCallLog.setCallTel(callLog.getCall_tel());
            }
            
            callLogList.add(riskCallLog);
        }
        
        
        return callLogList;
    }
}
