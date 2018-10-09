package com.mo9.raptor.engine.state.handler.user;

import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.user.ManualAuditEvent;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import com.mo9.raptor.entity.UserEntity;
import org.springframework.stereotype.Component;

/**
 * Created by jyou on 2018/10/9.
 *
 * @author jyou
 * 人工审核事件
 */
@Component("userAuditingState")
@StateHandler(name = StatusEnum.MANUAL)

public class ManualStateHandler implements IStateHandler<UserEntity> {
    @Override
    public UserEntity handle(UserEntity user, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {
        if (event instanceof ManualAuditEvent) {
            ManualAuditEvent manualAuditEvent = (ManualAuditEvent) event;
            boolean pass = manualAuditEvent.isPass();
            if(pass){
                user.setStatus(StatusEnum.PASSED.name());
            }else{
                user.setStatus(StatusEnum.REJECTED.name());
            }
            user.setDescription(user.getDescription()  + event.getEventTime() + ":" + manualAuditEvent.getExplanation() + ";");
        }else {
            throw new InvalidEventException("用户状态事件类型不匹配，状态：" + user.getStatus() + "，事件：" + event);
        }
        return user;
    }
}
