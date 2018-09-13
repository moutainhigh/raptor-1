package com.mo9.raptor.engine.action.impl;

import com.mo9.raptor.engine.action.IAction;
import com.mo9.raptor.engine.event.impl.order.loan.LendLaunchEvent;
import com.mo9.raptor.engine.launcher.IEventLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by gqwu on 2018/4/4.
 * 发起放款
 */
public class LendLaunchAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(LendLaunchAction.class);

    private String loanOrderId;

    private IEventLauncher eventLauncher;

    public LendLaunchAction(String loanOrderId, IEventLauncher eventLauncher) {
        this.loanOrderId = loanOrderId;
        this.eventLauncher = eventLauncher;
    }

    @Override
    public void run(){
        LendLaunchEvent lendLaunchEvent = new LendLaunchEvent(loanOrderId);
        try {
            eventLauncher.launch(lendLaunchEvent);
        } catch (Exception e) {
            logger.error("自动发送发起放款事件异常，事件：[{}]", lendLaunchEvent, e);
        }
    }

    @Override
    public String getActionType() {
        return this.getClass().getName();
    }

    @Override
    public String getOrderId() {
        return loanOrderId;
    }
}
