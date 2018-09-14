package com.mo9.raptor.engine.state.event.impl;

import com.mo9.raptor.engine.state.event.AbstractStateEvent;
import com.mo9.raptor.engine.state.event.IStateEvent;

/**
 * Created by gqwu on 2018/4/4.
 * 恢复订单
 */
public class RecoveringEvent extends AbstractStateEvent implements IStateEvent {
    public RecoveringEvent(String orderId) {
        super(orderId);
    }
}
