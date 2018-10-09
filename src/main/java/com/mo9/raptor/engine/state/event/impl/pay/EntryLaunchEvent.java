package com.mo9.raptor.engine.state.event.impl.pay;

import com.mo9.raptor.engine.state.event.AbstractStateEvent;
import com.mo9.raptor.engine.state.event.IStateEvent;

/**
 * Created by gqwu on 2018/4/8.
 */
public class EntryLaunchEvent extends AbstractStateEvent implements IStateEvent {

    public EntryLaunchEvent(String payOrderId) {
        super(payOrderId);
    }
}
