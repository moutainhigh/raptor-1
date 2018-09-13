package com.mo9.raptor.engine.event.impl.order.loan;

import com.mo9.raptor.engine.event.AbstractStateEvent;
import com.mo9.raptor.engine.event.IStateEvent;
import com.mo9.raptor.engine.structure.Scheme;

/**
 * Created by gqwu on 2018/4/4.
 * 单一订单入账明细事件
 */
public class SchemeEntryEvent extends AbstractStateEvent implements IStateEvent {

    private final Scheme entryScheme;

    private final String entryType;

    public SchemeEntryEvent(String loanOrderId, String entryType, Scheme entryScheme) {
        super(loanOrderId);
        this.entryType = entryType;
        this.entryScheme = entryScheme;
    }

    public String getEntryType () {
        return this.entryType;
    }

    public Scheme getEntryScheme() {
        return entryScheme;
    }
}
