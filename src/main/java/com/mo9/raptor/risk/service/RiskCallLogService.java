package com.mo9.raptor.risk.service;

import com.mo9.raptor.risk.entity.TRiskCallLog;
import org.springframework.stereotype.Service;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 13:43 .
 */

public interface RiskCallLogService {
    
    void save(TRiskCallLog riskCallLog);
}
