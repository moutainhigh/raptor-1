package com.mo9.raptor.engine.state.action.impl.loan;

import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.engine.state.event.impl.lend.LendResponseEvent;
import com.mo9.raptor.engine.state.event.impl.loan.LoanResponseEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by gqwu on 2018/4/4.
 * 放款结果Action
 */
public class LoanResponseAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(LoanResponseAction.class);

    private LendResponseEvent event;

    private IEventLauncher loanEventLauncher;

    public LoanResponseAction(LendResponseEvent event,IEventLauncher loanEventLauncher) {
        this.event = event;
        this.loanEventLauncher = loanEventLauncher;
    }

    @Override
    public void run(){
        boolean succeeded = event.isSucceeded();
        LoanResponseEvent loanResponse = new LoanResponseEvent(
                event.getEntityUniqueId(),
                event.getActualLent(),
                succeeded,
                event.getSuccessTime(),
                event.getLendSignature(),
                event.getExplanation());
        try {
            loanEventLauncher.launch(loanResponse);
        } catch (Exception e) {
            logger.error("放款通知借款订单事件异常，事件：[{}]", loanResponse, e);
        }
    }

    @Override
    public String getActionType() {
        return this.getClass().getName();
    }

    @Override
    public String getOrderId() {
        return event.getEntityUniqueId();
    }
}
