package com.mo9.raptor.engine.action.impl;

import com.mo9.raptor.engine.action.IAction;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.event.impl.order.pay.EntryLaunchEvent;
import com.mo9.raptor.engine.launcher.IEventLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntryLaunchAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(EntryLaunchAction.class);

    private String payOrderId;

    private IEventLauncher payOrderEventLauncher;

    private IPayOrderService payOrderService;

    public EntryLaunchAction(String payOrderId, IEventLauncher payOrderEventLauncher, IPayOrderService payOrderService) {
        this.payOrderId = payOrderId;
        this.payOrderEventLauncher = payOrderEventLauncher;
        this.payOrderService = payOrderService;
    }

    @Override
    public void run() {
        EntryLaunchEvent entryLaunchEvent = new EntryLaunchEvent(payOrderId);
        try {
            PayOrderEntity payOrder = payOrderService.getByOrderId(payOrderId);

            this.payOrderEventLauncher.launch(entryLaunchEvent);
        } catch (Exception e) {
            logger.error("自动发送发起入账事件异常，事件：[{}]", entryLaunchEvent, e);
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
