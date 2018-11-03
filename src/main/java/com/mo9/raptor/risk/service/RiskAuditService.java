package com.mo9.raptor.risk.service;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;

import java.util.List;

/**
 * @author yngong
 */
public interface RiskAuditService {

    AuditResponseEvent audit(String userCode);

    /**
     * 手动调用接口时调用，会把结果上传到oss，不做任何记录
     * @param userCode
     * @return
     */
    JSONObject manualAudit(String userCode);
}
