package com.mo9.raptor.engine.state.event.impl.loan;

import com.mo9.raptor.engine.state.event.AbstractStateEvent;
import com.mo9.raptor.engine.state.event.IStateEvent;
import com.mo9.raptor.engine.structure.Scheme;

/**
 * Created by gqwu on 2018/4/4.
 * 单一订单入账明细事件
 */
public class LoanEntryEvent extends AbstractStateEvent implements IStateEvent {

    private final Scheme entryScheme;

    private final String payType;

    private final String payOrderId;

    public LoanEntryEvent(String loanOrderId, String payOrderId, String payType, Scheme entryScheme) {
        super(loanOrderId);
        this.payType = payType;
        this.payOrderId = payOrderId;
        this.entryScheme = entryScheme;
    }

    public String getPayType() {
        return payType;
    }

    public Scheme getEntryScheme() {
        return entryScheme;
    }

    public String getPayOrderId() {
        return payOrderId;
    }
}
