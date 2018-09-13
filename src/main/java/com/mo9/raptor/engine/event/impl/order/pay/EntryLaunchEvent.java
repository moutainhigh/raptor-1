package com.mo9.raptor.engine.event.impl.order.pay;

import com.mo9.raptor.engine.event.AbstractStateEvent;
import com.mo9.raptor.engine.event.IStateEvent;

/**
 * Created by gqwu on 2018/4/8.
 */
public class EntryLaunchEvent extends AbstractStateEvent implements IStateEvent {

    public EntryLaunchEvent(String payOrderId) {
        super(payOrderId);
    }
}
