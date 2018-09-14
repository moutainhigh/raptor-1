package com.mo9.raptor.engine.state.handler.pay;

import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.state.action.impl.pay.PayAuditAction;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.event.impl.AuditLaunchEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component
@StateHandler(name = StatusEnum.PENDING)
public class PendingStateHandler implements IStateHandler<PayOrderEntity> {

    private static final Logger logger = LoggerFactory.getLogger(PendingStateHandler.class);

    @Autowired
    private IEventLauncher payEventLauncher;

    @Override
    public PayOrderEntity handle(PayOrderEntity payOrder, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        if (event instanceof AuditLaunchEvent) {
            payOrder.setStatus(StatusEnum.AUDITING.name());
            /** 还款订单审核 */
            actionExecutor.append(new PayAuditAction(payOrder, payEventLauncher));
        } else {
            throw new InvalidEventException("还款订单状态与事件类型不匹配，状态：" + payOrder.getStatus() + "，事件：" + event);
        }

        return payOrder;
    }
}
