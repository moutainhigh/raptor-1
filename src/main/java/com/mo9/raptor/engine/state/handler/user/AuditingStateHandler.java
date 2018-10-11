package com.mo9.raptor.engine.state.handler.user;

import com.mo9.raptor.engine.enums.AuditResultEnum;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.state.action.impl.user.UserPushAction;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.utils.push.PushBean;
import com.mo9.raptor.utils.push.PushUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component("userAuditingState")
@StateHandler(name = StatusEnum.AUDITING)
class AuditingStateHandler implements IStateHandler<UserEntity> {

    @Resource
    private PushUtils pushUtils;

    @Override
    public UserEntity handle(UserEntity user, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        if (event instanceof AuditResponseEvent) {
            AuditResponseEvent auditResponseEvent = (AuditResponseEvent) event;
            AuditResultEnum auditResultEnum = auditResponseEvent.getAuditResultEnum();
            if (auditResultEnum == AuditResultEnum.MANUAL) {
                user.setStatus(StatusEnum.MANUAL.name());
            }else if(auditResultEnum == AuditResultEnum.PASS){
                user.setStatus(StatusEnum.PASSED.name());
                PushBean pushBean = new PushBean(user.getUserCode(), "审核通过", "恭喜你，审核通过，开始借款吧~");
                actionExecutor.append(new UserPushAction(user.getUserCode(), pushUtils, pushBean));
            } else if(auditResultEnum == AuditResultEnum.REJECTED){
                user.setStatus(StatusEnum.REJECTED.name());
                PushBean pushBean = new PushBean(user.getUserCode(), "审核拒绝", "很遗憾，审核拒绝，请30天后再来吧~");
                actionExecutor.append(new UserPushAction(user.getUserCode(), pushUtils, pushBean));
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
