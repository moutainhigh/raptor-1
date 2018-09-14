package com.mo9.raptor.engine.state.event.impl.loan;

import com.mo9.raptor.engine.state.event.AbstractStateEvent;
import com.mo9.raptor.engine.state.event.IStateEvent;

/**
 * Created by gqwu on 2018/4/4.
 * 放款命令
 */
public class LoanLaunchEvent extends AbstractStateEvent implements IStateEvent {
    public LoanLaunchEvent(String loanOrderId) {
        super(loanOrderId);
    }
}
