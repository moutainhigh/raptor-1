package com.mo9.raptor.risk.service;

import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;

/**
 * @author yngong
 */
public interface RiskAuditService {

    AuditResponseEvent audit(String userCode);
}
