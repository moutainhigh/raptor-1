package com.mo9.raptor.engine.state.action.impl.loan;

import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.engine.state.event.impl.loan.LoanLaunchEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by gqwu on 2018/4/4.
 * 发起放款
 */
public class LoanLaunchAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(LoanLaunchAction.class);

    private String loanOrderId;

    private IEventLauncher loanEventLauncher;

    public LoanLaunchAction(String loanOrderId, IEventLauncher loanEventLauncher) {
        this.loanOrderId = loanOrderId;
        this.loanEventLauncher = loanEventLauncher;
    }

    @Override
    public void run(){
        LoanLaunchEvent event = new LoanLaunchEvent(loanOrderId);
        try {
            loanEventLauncher.launch(event);
        } catch (Exception e) {
            logger.error("自动发送发起放款事件异常，事件：[{}]", event, e);
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
