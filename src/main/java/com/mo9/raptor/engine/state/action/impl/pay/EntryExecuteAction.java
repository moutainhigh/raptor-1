package com.mo9.raptor.engine.state.action.impl.pay;

import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.engine.state.event.impl.loan.LoanEntryEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.engine.structure.Scheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntryExecuteAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(EntryExecuteAction.class);

    private String batchId;

    private IEventLauncher loanEventLauncher;

    public EntryExecuteAction(String batchId, IEventLauncher loanEventLauncher) {
        this.batchId = batchId;
        this.loanEventLauncher = loanEventLauncher;
    }

    @Override
    public void run() {

        /** TODO：根据批次号，查询还款订单，并根据还款订单，创建对应的借款订单入账事件 */

        String loanOrderId = "";
        String payType = "";
        Scheme scheme = new Scheme();

        LoanEntryEvent event = new LoanEntryEvent(loanOrderId, payType, scheme);
        try {
            this.loanEventLauncher.launch(event);
        } catch (Exception e) {
            logger.error("用户入账事件处理异常，事件：[{}]", event, e);
        }

    }

    @Override
    public String getActionType() {
        return this.getClass().getName();
    }

    @Override
    public String getOrderId() {
        //TODO:批次号
        return this.batchId;
    }
}
