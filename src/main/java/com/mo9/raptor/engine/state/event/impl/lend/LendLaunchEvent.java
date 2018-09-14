package com.mo9.raptor.engine.state.event.impl.lend;

import com.mo9.raptor.engine.state.event.AbstractStateEvent;
import com.mo9.raptor.engine.state.event.IStateEvent;

/**
 * Created by gqwu on 2018/4/4.
 * 放款命令
 */
public class LendLaunchEvent extends AbstractStateEvent implements IStateEvent {
    public LendLaunchEvent(String lendOrderId) {
        super(lendOrderId);
    }
}
