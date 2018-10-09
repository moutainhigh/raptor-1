package com.mo9.raptor.engine.state.event.impl;

import com.mo9.raptor.engine.enums.AuditResultEnum;
import com.mo9.raptor.engine.state.event.AbstractStateEvent;
import com.mo9.raptor.engine.state.event.IStateEvent;

/**
 * Created by gqwu on 2018/4/4.
 * 审核结果
 */
public class AuditResponseEvent extends AbstractStateEvent implements IStateEvent {

    private boolean isPass;

    private final String explanation;

    private AuditResultEnum auditResultEnum;

    /**
     * 订单状态机专业
     * @param orderId
     * @param isPass
     * @param explanation
     */
    public AuditResponseEvent(String orderId, boolean isPass, String explanation) {
        super(orderId);
        this.isPass = isPass;
        this.explanation = explanation;
    }

    /**
     * 用户状态机专用
     * @param userCode
     * @param explanation
     * @param auditResultEnum
     */
    public AuditResponseEvent(String userCode, String explanation, AuditResultEnum auditResultEnum) {
        super(userCode);
        this.explanation = explanation;
        this.auditResultEnum = auditResultEnum;
    }

    public boolean isPass() {
        return isPass;
    }

    public String getExplanation() {
        return explanation;
    }

    public AuditResultEnum getAuditResultEnum() {
        return auditResultEnum;
    }

    @Override
    public String toString() {
        return "AuditResponseEvent{" +
                "isPass=" + isPass +
                ", explanation='" + explanation + '\'' +
                '}';
    }
}
