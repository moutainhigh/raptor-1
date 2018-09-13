package com.mo9.raptor.engine.action.impl;

import com.mo9.raptor.engine.action.IAction;
import com.mo9.raptor.engine.event.impl.order.pay.DeductLaunchEvent;
import com.mo9.raptor.engine.launcher.IEventLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeductLaunchAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(DeductLaunchAction.class);

    private String payOrderId;

    private IEventLauncher payOrderEventLauncher;

    public DeductLaunchAction(String payOrderId, IEventLauncher payOrderEventLauncher) {
        this.payOrderId = payOrderId;
        this.payOrderEventLauncher = payOrderEventLauncher;
    }

    @Override
    public void run() {
        DeductLaunchEvent deductLaunchEvent = new DeductLaunchEvent(payOrderId);

        try {
            this.payOrderEventLauncher.launch(deductLaunchEvent);
        } catch (Exception e) {
            logger.error("自动发送发起扣款事件异常，事件：[{}]", deductLaunchEvent, e);
        }
    }

    @Override
    public String getActionType() {
        return this.getClass().getName();
    }

    @Override
    public String getOrderId() {
        return payOrderId;
    }

}
