package com.mo9.raptor.engine.state.action.impl.loan;

import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by gqwu on 2018/4/4.
 * 完成贷款订单审核，并发送审核结果响应事件
 */
public class LoanAuditAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(LoanAuditAction.class);

    private LoanOrderEntity loanOrder;

    private IEventLauncher loanEventLauncher;

    public LoanAuditAction(LoanOrderEntity loanOrder, IEventLauncher loanEventLauncher) {

        this.loanOrder = loanOrder;
        this.loanEventLauncher = loanEventLauncher;
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
            loanEventLauncher.launch(event);
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
