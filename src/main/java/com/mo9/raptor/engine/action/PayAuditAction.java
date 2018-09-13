package com.mo9.raptor.engine.action;

import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.event.pay.DeductLaunchEvent;
import com.mo9.raptor.engine.launcher.IEventLauncher;
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

        DeductLaunchEvent event = new DeductLaunchEvent(payOrder.getOrderId());

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
