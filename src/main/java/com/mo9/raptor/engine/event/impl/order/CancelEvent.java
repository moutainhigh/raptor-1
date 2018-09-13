package com.mo9.raptor.engine.event.impl.order;

import com.mo9.raptor.engine.event.AbstractStateEvent;
import com.mo9.raptor.engine.event.IStateEvent;

/**
 * Created by gqwu on 2018/4/4.
 * 用户取消
 */
public class CancelEvent extends AbstractStateEvent implements IStateEvent {
    public CancelEvent(String orderId) {
        super(orderId);
    }
}
