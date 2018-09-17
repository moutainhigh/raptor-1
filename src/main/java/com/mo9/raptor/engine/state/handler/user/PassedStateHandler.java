package com.mo9.raptor.engine.state.handler.user;

import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.user.BlackEvent;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import com.mo9.raptor.entity.UserEntity;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component
@StateHandler(name = StatusEnum.PASSED)
class PassedStateHandler implements IStateHandler<UserEntity> {

    @Override
    public UserEntity handle(UserEntity user, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        if (event instanceof BlackEvent) {
            BlackEvent blackEvent = (BlackEvent) event;
            user.setStatus(StatusEnum.BLACK.name());
            user.setDescription(user.getDescription() + " " + event.getEventTime() + ":" + blackEvent.getExplanation());
        } else {
            throw new InvalidEventException("用户状态与事件类型不匹配，状态：" + user.getStatus() + "，事件：" + event);
        }

        return user;
    }
}
