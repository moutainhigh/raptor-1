package com.mo9.raptor.engine.action.impl;

import com.mo9.raptor.engine.action.IAction;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.event.impl.order.loan.LendExecuteEvent;
import com.mo9.raptor.engine.launcher.IEventLauncher;
import com.mo9.raptor.engine.service.ILoanOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by gqwu on 2018/4/4.
 * 实际执行放款操作的行为
 */
public class LendExecuteAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(LendExecuteAction.class);

    private String orderId;

    private ILoanOrderService loanOrderService;

    private IEventLauncher lendEventLauncher;

    public LendExecuteAction(String orderId, ILoanOrderService loanOrderService, IEventLauncher lendEventLauncher) {
        this.orderId = orderId;
        this.loanOrderService = loanOrderService;
        this.lendEventLauncher = lendEventLauncher;
    }

    @Override
    public void run() {
        try {
            LoanOrderEntity loanOrder = loanOrderService.getByOrderId(orderId);
            LendExecuteEvent event = new LendExecuteEvent(loanOrder);

            lendEventLauncher.launch(event);
        } catch (Exception e) {
            logger.error("下单后, orderId: {}放款时发生错误:", this.orderId, e);
        }
    }

    @Override
    public String getActionType() {
        return this.getClass().getName();
    }

    @Override
    public String getOrderId() {
        return orderId;
    }
}
