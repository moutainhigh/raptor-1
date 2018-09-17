package com.mo9.raptor.engine.state.event.impl.user;

import com.mo9.raptor.engine.state.event.AbstractStateEvent;
import com.mo9.raptor.engine.state.event.IStateEvent;

/**
 * Created by gqwu on 2018/4/4.
 * 发起审核
 */
public class CollectLaunchEvent extends AbstractStateEvent implements IStateEvent {

    public CollectLaunchEvent(String userCode) {
        super(userCode);
    }
}
