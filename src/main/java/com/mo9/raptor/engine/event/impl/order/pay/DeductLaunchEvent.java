package com.mo9.raptor.engine.event.impl.order.pay;

import com.mo9.raptor.engine.event.AbstractStateEvent;
import com.mo9.raptor.engine.event.IStateEvent;

/**
 * Created by gqwu on 2018/4/8.
 */
public class DeductLaunchEvent extends AbstractStateEvent implements IStateEvent {

    public DeductLaunchEvent(String payOrderId) {
        super(payOrderId);
    }
}
