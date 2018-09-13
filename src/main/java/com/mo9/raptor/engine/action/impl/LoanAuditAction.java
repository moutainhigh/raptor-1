package com.mo9.raptor.engine.action.impl;

import com.mo9.raptor.engine.action.IAction;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.event.IEvent;
import com.mo9.raptor.engine.event.impl.order.AuditResponseEvent;
import com.mo9.raptor.engine.launcher.IEventLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by gqwu on 2018/4/4.
 * 贷款订单审核行为
 */
public class LoanAuditAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(LoanAuditAction.class);

    private LoanOrderEntity loanOrder;

    private IEventLauncher eventLauncher;

    public LoanAuditAction(LoanOrderEntity loanOrder, IEventLauncher eventLauncher) {

        this.loanOrder = loanOrder;
        this.eventLauncher = eventLauncher;
    }

    @Override
    public void run() {

        IEvent event;

        if (loanOrder.verify()) {
            event = new AuditResponseEvent(loanOrder.getOrderId(), true, "审核通过");
        } else {
            event = new AuditResponseEvent(loanOrder.getOrderId(), false, "审核拒绝");
        }

        try {
            eventLauncher.launch(event);
        } catch (Exception e) {
            logger.error("审核结果事件发送异常，事件：[{}]", event, e);
        }
    }

    @Override
    public String getActionType() {
        return this.getClass().getName();
    }

    @Override
    public String getOrderId() {
        return loanOrder.getOrderId();
    }

}
