package com.mo9.raptor.engine.event.impl.order;

import com.mo9.raptor.engine.event.AbstractStateEvent;
import com.mo9.raptor.engine.event.IStateEvent;

/**
 * Created by gqwu on 2018/4/4.
 * 恢复订单
 */
public class RecoveringEvent extends AbstractStateEvent implements IStateEvent {
    public RecoveringEvent(String orderId) {
        super(orderId);
    }
}
