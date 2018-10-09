package com.mo9.raptor.risk.service;

import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;

/**
 * @author wtwei .
 * @date 2018/10/8 .
 * @time 15:07 .
 */
public interface RiskRuleEngineService {

    /**
     * 入网时间规则
     * @param userCode
     * @return
     */
    AuditResponseEvent openDateRule(String userCode);

    /**
     * 紧急联系人通话次数规则
     * @param userCode
     * @return
     */
    AuditResponseEvent mergencyCallTimesRule(String userCode);

    /**
     * 紧急联系人有无未完成订单
     * @param userCode
     * @return
     */
    AuditResponseEvent mergencyHadNoDoneOrderRule(String userCode);

    /**
     * 紧急联系人有没有命中江湖救急黑名单
     * @param userCode
     * @return
     */
    AuditResponseEvent mergencyInJHJJBlackListRule(String userCode);

    /**
     * 手机号被同一家贷款机构呼叫次数
     * @param userCode
     * @return
     */
    AuditResponseEvent calledTimesByOneLoanCompanyRule(String userCode);

    /**
     * 手机号被不同贷款机构呼叫次数
     * @param userCode
     * @return
     */
    AuditResponseEvent calledTimesByDifferentLoanCompanyRule(String userCode);
}
