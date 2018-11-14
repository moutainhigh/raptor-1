package com.mo9.raptor.engine.state.handler.user;

import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.state.action.impl.user.UserPushAction;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.user.ManualAuditEvent;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.utils.push.PushBean;
import com.mo9.raptor.utils.push.PushUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by jyou on 2018/10/9.
 *
 * @author jyou
 * 人工审核事件
 */
@Component
@StateHandler(name = StatusEnum.MANUAL)
public class ManualStateHandler implements IStateHandler<UserEntity> {

    @Resource
    private PushUtils pushUtils;

    @Override
    public UserEntity handle(UserEntity user, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {
        if (event instanceof ManualAuditEvent) {
            ManualAuditEvent manualAuditEvent = (ManualAuditEvent) event;
            boolean pass = manualAuditEvent.isPass();
            if(pass){
                user.setStatus(StatusEnum.PASSED.name());
                PushBean pushBean = new PushBean(user.getUserCode(), "审核通过", "恭喜你，审核通过，开始借款吧~");
                actionExecutor.append(new UserPushAction(user.getUserCode(), pushUtils, pushBean));
            }else{
                user.setStatus(StatusEnum.REJECTED.name());
                PushBean pushBean = new PushBean(user.getUserCode(), "审核拒绝", "很遗憾，审核拒绝，请30天后再来吧~");
                actionExecutor.append(new UserPushAction(user.getUserCode(), pushUtils, pushBean));
            }
            user.setDescription(user.getDescription()  + event.getEventTime() + ":" + manualAuditEvent.getExplanation() + ";");
        }else {
            throw new InvalidEventException("用户状态事件类型不匹配，状态：" + user.getStatus() + "，事件：" + event);
        }
        return user;
    }
}
