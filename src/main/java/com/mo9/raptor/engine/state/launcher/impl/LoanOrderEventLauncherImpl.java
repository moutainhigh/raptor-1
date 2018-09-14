package com.mo9.raptor.engine.state.launcher.impl;

import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.state.event.IStateEvent;
import com.mo9.raptor.engine.state.launcher.AbstractStateEventLauncher;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.engine.service.ILoanOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component("loanOrderEventLauncher")
public class LoanOrderEventLauncherImpl extends AbstractStateEventLauncher<LoanOrderEntity, IStateEvent> implements IEventLauncher<IStateEvent> {

    @Autowired
    private ILoanOrderService loanOrderService;

    @Override
    public LoanOrderEntity selectEntity(String entityUniqueId) {
        return this.loanOrderService.getByOrderId(entityUniqueId);
    }

    @Override
    public void saveEntity(LoanOrderEntity entity) {
        entity.setUpdateTime(System.currentTimeMillis());
        loanOrderService.save(entity);
    }
}
