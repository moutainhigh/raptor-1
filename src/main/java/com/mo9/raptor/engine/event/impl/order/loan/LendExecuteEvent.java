package com.mo9.raptor.engine.event.impl.order.loan;

import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.event.AbstractStateEvent;
import com.mo9.raptor.engine.event.IStateEvent;

/**
 * Created by gqwu on 2018/4/4.
 * 放款命令
 */
public class LendExecuteEvent extends AbstractStateEvent implements IStateEvent {
    public LendExecuteEvent(LoanOrderEntity loanOrder) {
        super(loanOrder.getOrderId());
    }
}
