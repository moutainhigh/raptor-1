package com.mo9.raptor.engine.state.handler.user;

import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.state.action.impl.user.UserAuditAction;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.AuditLaunchEvent;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component
@StateHandler(name = StatusEnum.COLLECTING)
class CollectingStateHandler implements IStateHandler<UserEntity> {

    @Autowired
    private IEventLauncher userEventLauncher;

    @Override
    public UserEntity handle(UserEntity user, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        if (event instanceof AuditLaunchEvent) {
            user.setStatus(StatusEnum.AUDITING.name());
            user.setDescription(user.getDescription() + " " + event.getEventTime());
            actionExecutor.append(new UserAuditAction(user.getUserCode(), userEventLauncher));
        } else {
            throw new InvalidEventException("还款订单状态与事件类型不匹配，状态：" + user.getStatus() + "，事件：" + event);
        }

        return user;
    }
}
