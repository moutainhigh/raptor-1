package com.mo9.raptor.engine.state.handler.user;

import com.mo9.raptor.engine.enums.AuditResultEnum;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import com.mo9.raptor.entity.UserEntity;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component("userAuditingState")
@StateHandler(name = StatusEnum.AUDITING)
class AuditingStateHandler implements IStateHandler<UserEntity> {

    @Override
    public UserEntity handle(UserEntity user, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        if (event instanceof AuditResponseEvent) {
            AuditResponseEvent auditResponseEvent = (AuditResponseEvent) event;
            AuditResultEnum auditResultEnum = auditResponseEvent.getAuditResultEnum();
            if (auditResultEnum == AuditResultEnum.MANUAL) {
                user.setStatus(StatusEnum.MANUAL.name());
            }else if(auditResultEnum == AuditResultEnum.PASS){
                user.setStatus(StatusEnum.PASSED.name());
            } else if(auditResultEnum == AuditResultEnum.REJECTED){
                user.setStatus(StatusEnum.REJECTED.name());
            }else{
                throw new InvalidEventException("用户状态事件类型不匹配，状态：" + user.getStatus() + "，事件：" + event);
            }
            user.setDescription(user.getDescription()  + event.getEventTime() + ":" + auditResponseEvent.getExplanation() + ";");

        } else {
            throw new InvalidEventException("用户状态事件类型不匹配，状态：" + user.getStatus() + "，事件：" + event);
        }

        return user;
    }
}
