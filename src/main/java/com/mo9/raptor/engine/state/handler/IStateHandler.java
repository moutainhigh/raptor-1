package com.mo9.raptor.engine.state.handler;

import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.entity.IStateEntity;
import com.mo9.raptor.engine.state.event.IEvent;

/**
 * Created by gqwu on 2018/4/19.
 */
public interface IStateHandler<E extends IStateEntity> {

    E handle(E entity, IEvent event, IActionExecutor actionExecutor)
            throws Exception;

}
