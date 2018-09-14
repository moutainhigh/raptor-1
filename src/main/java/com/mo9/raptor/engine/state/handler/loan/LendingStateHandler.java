package com.mo9.raptor.engine.state.handler.loan;

import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.loan.LoanResponseEvent;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component
@StateHandler(name = StatusEnum.LENDING)
public class LendingStateHandler implements IStateHandler<LoanOrderEntity> {

    private static final Logger logger = LoggerFactory.getLogger(LendingStateHandler.class);

    @Override
    public LoanOrderEntity handle(LoanOrderEntity loanOrder, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        if (event instanceof LoanResponseEvent) {
            LoanResponseEvent lendResponseEvent = (LoanResponseEvent) event;

            BigDecimal lentNumber = lendResponseEvent.getActualLent();

            if (lendResponseEvent.isSucceeded()) {
                loanOrder.setStatus(StatusEnum.LENT.name());
                loanOrder.setLentNumber(lentNumber);
                loanOrder.setLendSignature(lendResponseEvent.getLendSignature());
                loanOrder.setLentTime(lendResponseEvent.getSuccessTime());
            } else {
                loanOrder.setStatus(StatusEnum.FAILED.name());
            }
            loanOrder.setDescription(loanOrder.getDescription() + " " + event.getEventTime() + ":" + lendResponseEvent.getExplanation());
        } else {
            throw new InvalidEventException("贷款订单状态与事件类型不匹配，状态：" + loanOrder.getStatus() + "，事件：" + event);
        }

        return loanOrder;
    }
}
