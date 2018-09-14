package com.mo9.raptor.engine.state.launcher.impl;

import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.state.event.IStateEvent;
import com.mo9.raptor.engine.state.launcher.AbstractStateEventLauncher;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component("lendEventLauncher")
public class LendEventLauncherImpl extends AbstractStateEventLauncher<LendOrderEntity, IStateEvent> implements IEventLauncher<IStateEvent> {

    @Autowired
    private ILendOrderService lendOrderService;

    @Override
    public LendOrderEntity selectEntity(String entityUniqueId) {
        return this.lendOrderService.getByOrderId(entityUniqueId);
    }

    @Override
    public void saveEntity(LendOrderEntity entity) {
        entity.setUpdateTime(System.currentTimeMillis());
        lendOrderService.save(entity);
    }
}
