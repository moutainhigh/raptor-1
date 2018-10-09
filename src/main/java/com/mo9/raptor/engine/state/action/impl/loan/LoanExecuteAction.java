package com.mo9.raptor.engine.state.action.impl.loan;

import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.engine.state.event.impl.lend.LendLaunchEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.entity.BankEntity;
import com.mo9.raptor.service.BankService;
import com.mo9.raptor.utils.IDWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by gqwu on 2018/4/4.
 * 贷款行为-创建放款订单，并发起放款
 */
public class LoanExecuteAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(LoanExecuteAction.class);

    private String orderId;

    private IEventLauncher lendEventLauncher;

    private BankService bankService;

    private ILoanOrderService loanOrderService;

    private ILendOrderService lendOrderService;

    private IDWorker idWorker;

    public LoanExecuteAction(String orderId, ILoanOrderService loanOrderService, ILendOrderService lendOrderService, BankService bankService, IEventLauncher lendEventLauncher, IDWorker idWorker) {
        this.orderId = orderId;
        this.bankService = bankService;
        this.loanOrderService = loanOrderService;
        this.lendOrderService = lendOrderService;
        this.lendEventLauncher = lendEventLauncher;
        this.idWorker = idWorker;
    }

    @Override
    public void run() {

        try {
            LendLaunchEvent event = new LendLaunchEvent(orderId);
            lendEventLauncher.launch(event);
        } catch (Exception e) {
            logger.error("下单后, orderId: {}放款时发生错误:", orderId, e);
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
