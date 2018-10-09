package com.mo9.raptor.engine.state.event.impl;

import com.mo9.raptor.engine.state.event.AbstractStateEvent;
import com.mo9.raptor.engine.state.event.IStateEvent;

/**
 * Created by gqwu on 2018/4/4.
 * 用户取消
 */
public class CancelEvent extends AbstractStateEvent implements IStateEvent {
    public CancelEvent(String orderId) {
        super(orderId);
    }
}
