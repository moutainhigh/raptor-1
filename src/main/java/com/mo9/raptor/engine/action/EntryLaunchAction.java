package com.mo9.raptor.engine.action;

import com.mo9.raptor.engine.event.pay.EntryLaunchEvent;
import com.mo9.raptor.engine.launcher.IEventLauncher;
import com.mo9.raptor.service.IPayOrderService;
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
