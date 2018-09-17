package com.mo9.raptor.engine.state.action.impl.pay;

import com.mo9.raptor.engine.calculator.ILoanCalculator;
import com.mo9.raptor.engine.calculator.LoanCalculatorFactory;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.engine.state.event.impl.loan.LoanEntryEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.engine.structure.Scheme;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.exception.LoanEntryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntryExecuteAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(EntryExecuteAction.class);

    private String payOrderId;

    private IEventLauncher loanEventLauncher;

    private IPayOrderService payOrderService;

    private ILoanOrderService loanOrderService;

    private LoanCalculatorFactory calculatorFactory;

    public EntryExecuteAction(String payOrderId, IPayOrderService payOrderService, ILoanOrderService loanOrderService, IEventLauncher loanEventLauncher, LoanCalculatorFactory calculatorFactory) {
        this.payOrderId = payOrderId;
        this.payOrderService = payOrderService;
        this.loanOrderService = loanOrderService;
        this.loanEventLauncher = loanEventLauncher;
        this.calculatorFactory = calculatorFactory;
    }

    @Override
    public void run() {

        PayOrderEntity payOrderEntity = payOrderService.getByOrderId(payOrderId);
        String loanOrderId = payOrderEntity.getLoanOrderId();
        LoanOrderEntity loanOrderEntity = loanOrderService.getByOrderId(loanOrderId);
        ILoanCalculator calculator = calculatorFactory.load(loanOrderEntity);
        String payType = payOrderEntity.getType();
        Item entryItem = null;
        try {
            entryItem = calculator.entryItem(System.currentTimeMillis(), payType, payOrderEntity.getPayNumber(), loanOrderEntity);
        } catch (LoanEntryException e) {
            logger.error("计算entryItem异常 ", e);
        }
        LoanEntryEvent event = new LoanEntryEvent(loanOrderId, payOrderId, payType, entryItem);
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
        return this.payOrderId;
    }
}
