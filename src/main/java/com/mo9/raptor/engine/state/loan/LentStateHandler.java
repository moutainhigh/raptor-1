package com.mo9.raptor.engine.state.loan;

import com.mo9.raptor.engine.action.IActionExecutor;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.event.IEvent;
import com.mo9.raptor.engine.event.impl.order.loan.SchemeEntryEvent;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.IStateHandler;
import com.mo9.raptor.engine.state.StateHandler;
import com.mo9.raptor.engine.structure.Scheme;
import com.mo9.raptor.enums.PayTypeEnum;
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

    @Override
    public LoanOrderEntity handle (LoanOrderEntity loanOrder, IEvent event, IActionExecutor actionExecutor)
            throws InvalidEventException, UnSupportTimeDiffException, MergeException {

        if (event instanceof SchemeEntryEvent) {
            SchemeEntryEvent schemeEntryEvent = (SchemeEntryEvent) event;
            ILoanCalculator loanCalculator = loanCalculatorFactory.load(loanOrder);
            Scheme originalScheme = loanCalculator.originScheme(loanOrder);
            Scheme realScheme = loanCalculator.realScheme(ClockFactory.clockTime(loanOrder.getOwnerId()), loanOrder);
            Scheme entryScheme = schemeEntryEvent.getEntryScheme();

            loanOrder = loanCalculator.schemeEntry(loanOrder, originalScheme, realScheme, entryScheme);
        } else {
            throw new InvalidEventException("贷款订单状态与事件类型不匹配，状态：" + loanOrder.getStatus() + "，事件：" + event);
        }
        return loanOrder;
    }
}
