package com.mo9.raptor.engine.state.launcher.impl;

import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.state.event.IStateEvent;
import com.mo9.raptor.engine.state.launcher.AbstractStateEventLauncher;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.engine.service.IPayOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component("payOrderEventLauncher")
public class PayOrderEventLauncherImpl extends AbstractStateEventLauncher<PayOrderEntity, IStateEvent> implements IEventLauncher<IStateEvent> {

    @Autowired
    private IPayOrderService payOrderService;

    @Override
    public PayOrderEntity selectEntity(String orderId) {
        return this.payOrderService.getByOrderId(orderId);
    }

    @Override
    public void saveEntity(PayOrderEntity entity) {
        entity.setUpdateTime(System.currentTimeMillis());
        this.payOrderService.save(entity);
    }
}
