package com.mo9.raptor.engine.state.launcher.impl;

import com.mo9.raptor.engine.state.event.IStateEvent;
import com.mo9.raptor.engine.state.launcher.AbstractStateEventLauncher;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("userEventLauncher")
public class UserEventLauncherImpl extends AbstractStateEventLauncher<UserEntity, IStateEvent> implements IEventLauncher<IStateEvent> {

    @Autowired
    private UserService userService;

    @Override
    public UserEntity selectEntity(String userCode) {
        return this.userService.findByUserCode(userCode);
    }

    @Override
    public void saveEntity(UserEntity entity) {
        entity.setUpdateTime(System.currentTimeMillis());
        this.userService.save(entity);
    }
}
