package com.mo9.raptor.engine.action.impl;

import com.mo9.raptor.engine.action.IAction;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.event.IEvent;
import com.mo9.raptor.engine.launcher.IEventLauncher;
import com.mo9.raptor.engine.statics.EngineStaticValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PayAuditAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(PayAuditAction.class);

    private PayOrderEntity payOrder;

    private IEventLauncher payOrderEventLauncher;

    public PayAuditAction(PayOrderEntity payOrder,
                          IEventLauncher payOrderEventLauncher) {
        this.payOrder = payOrder;
        this.payOrderEventLauncher = payOrderEventLauncher;
    }

    @Override
    public void run() {

        /** 还款订单过期检查 */
        long passSecond = (System.currentTimeMillis() - payOrder.getCreateTime()) / EngineStaticValue.SECOND_MILLS;

        IEvent event;

        if (passSecond > LoanLimitation.CONFIRM_PAY_EXPIRE_SECONDS) {
            event = new OrderExpireEvent(payOrder.getOrderId());
        } else {
            event = new AuditResponseEvent(payOrder.getOrderId(), true, "审核通过");
        }

        try {
            payOrderEventLauncher.launch(event);
        } catch (Exception e) {
            logger.error("还款订单审核结果事件发送异常，事件：[{}]", event, e);
        }
    }

    @Override
    public String getActionType() {
        return this.getClass().getName();
    }

    @Override
    public String getOrderId() {
        return payOrder.getOrderId();
    }
}
