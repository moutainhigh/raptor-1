package com.mo9.raptor.engine.state.event.impl.pay;

import com.mo9.raptor.engine.state.event.AbstractStateEvent;
import com.mo9.raptor.engine.state.event.IStateEvent;

import java.math.BigDecimal;

/**
 * Created by gqwu on 2018/4/8.
 */
public class EntryResponseEvent extends AbstractStateEvent implements IStateEvent {

    private final BigDecimal actualEntry;

    public EntryResponseEvent(String payOrderId, BigDecimal actualEntry) {
        super(payOrderId);
        this.actualEntry = actualEntry;
    }

    public BigDecimal getActualEntry() {
        return actualEntry;
    }
}
