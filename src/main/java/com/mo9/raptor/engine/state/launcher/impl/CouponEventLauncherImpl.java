package com.mo9.raptor.engine.state.launcher.impl;

import com.mo9.raptor.engine.entity.CouponEntity;
import com.mo9.raptor.engine.service.CouponService;
import com.mo9.raptor.engine.state.event.IStateEvent;
import com.mo9.raptor.engine.state.launcher.AbstractStateEventLauncher;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component("couponEventLauncher")
public class CouponEventLauncherImpl extends AbstractStateEventLauncher<CouponEntity, IStateEvent> implements IEventLauncher<IStateEvent> {

    @Autowired
    private CouponService couponService;

    @Override
    public CouponEntity selectEntity(String entityUniqueId) {
        return this.couponService.getByCouponId(entityUniqueId);
    }

    @Override
    public void saveEntity(CouponEntity entity) {
        entity.setUpdateTime(System.currentTimeMillis());
        couponService.save(entity);
    }
}
