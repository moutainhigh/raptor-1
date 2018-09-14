package com.mo9.raptor.engine.state.launcher;

import com.mo9.raptor.engine.state.event.IEvent;

/**
 * Created by gqwu on 2018/4/5.
 */
public interface IEventLauncher<V extends IEvent> {
    void launch(V event) throws Exception;
}
