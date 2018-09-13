package com.mo9.raptor.engine.action;

import com.mo9.raptor.engine.event.pay.EntryEvent;
import com.mo9.raptor.engine.launcher.IEventLauncher;
import com.mo9.raptor.service.IPayOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntryLaunchAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(EntryLaunchAction.class);

    private String payOrderId;

    private IEventLauncher entryEventLauncher;

    private IPayOrderService payOrderService;

    public EntryLaunchAction(String payOrderId, IEventLauncher entryEventLauncher, IPayOrderService payOrderService) {
        this.payOrderId = payOrderId;
        this.entryEventLauncher = entryEventLauncher;
        this.payOrderService = payOrderService;
    }

    @Override
    public void run() {
        EntryEvent entryEvent = new EntryEvent(payOrderId);
        try {
            this.entryEventLauncher.launch(entryEvent);
        } catch (Exception e) {
            logger.error("自动发送发起入账事件异常，事件：[{}]", entryEvent, e);
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
