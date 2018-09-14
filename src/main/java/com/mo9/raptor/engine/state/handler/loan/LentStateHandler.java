package com.mo9.raptor.engine.state.handler.loan;

import com.mo9.raptor.engine.calculator.ILoanCalculator;
import com.mo9.raptor.engine.calculator.LoanCalculatorFactory;
import com.mo9.raptor.engine.exception.MergeException;
import com.mo9.raptor.engine.exception.UnSupportTimeDiffException;
import com.mo9.raptor.engine.simulator.ClockFactory;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.state.action.impl.loan.EntryResponseAction;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.event.impl.loan.LoanEntryEvent;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.engine.structure.Scheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component
@StateHandler(name = StatusEnum.LENT)
public class LentStateHandler implements IStateHandler<LoanOrderEntity> {

    @Autowired
    LoanCalculatorFactory loanCalculatorFactory;

    @Autowired
    IEventLauncher payEventLauncher;

    @Override
    public LoanOrderEntity handle (LoanOrderEntity loanOrder, IEvent event, IActionExecutor actionExecutor)
            throws InvalidEventException, UnSupportTimeDiffException, MergeException {

        if (event instanceof LoanEntryEvent) {
            LoanEntryEvent loanEntryEvent = (LoanEntryEvent) event;
            ILoanCalculator loanCalculator = loanCalculatorFactory.load(loanOrder);
            Scheme originalScheme = loanCalculator.originScheme(loanOrder);
            Scheme realScheme = loanCalculator.realScheme(ClockFactory.clockTime(loanOrder.getOwnerId()), loanOrder);
            Scheme entryScheme = loanEntryEvent.getEntryScheme();

            loanOrder = loanCalculator.schemeEntry(loanOrder, originalScheme, realScheme, entryScheme);

            actionExecutor.append(new EntryResponseAction(loanEntryEvent.getPayOrderId(), entryScheme.sum(), payEventLauncher));
        } else {
            throw new InvalidEventException("贷款订单状态与事件类型不匹配，状态：" + loanOrder.getStatus() + "，事件：" + event);
        }
        return loanOrder;
    }
}
