package com.mo9.raptor.engine.state.action.impl.pay;

import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PayAuditAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(PayAuditAction.class);

    private PayOrderEntity payOrder;

    private IEventLauncher payEventLauncher;

    public PayAuditAction(PayOrderEntity payOrder, IEventLauncher payEventLauncher) {
        this.payOrder = payOrder;
        this.payEventLauncher = payEventLauncher;
    }

    @Override
    public void run() {

        IEvent event;

        if (payOrder.verify()) {
            event = new AuditResponseEvent(payOrder.getOrderId(), true, "审核通过");
        } else {
            event = new AuditResponseEvent(payOrder.getOrderId(), false, "审核拒绝");
        }

        try {
            payEventLauncher.launch(event);
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
