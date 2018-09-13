package com.mo9.raptor.engine.event.pay;

import com.mo9.raptor.engine.event.AbstractStateEvent;
import com.mo9.raptor.engine.event.IStateEvent;

/**
 * Created by gqwu on 2018/4/8.
 */
public class EntryEvent extends AbstractStateEvent implements IStateEvent {

    public EntryEvent(String payOrderId) {
        super(payOrderId);
    }
}
