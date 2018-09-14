package com.mo9.raptor.engine.state.handler.pay;

import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.RecoveringEvent;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component
@StateHandler(name = StatusEnum.CANCELLED)
public class CancelledStateHandler implements IStateHandler<PayOrderEntity> {

    @Override
    public PayOrderEntity handle(PayOrderEntity payOrder, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        /** 恢复订单 */
        if (event instanceof RecoveringEvent) {
            payOrder.setStatus(StatusEnum.PENDING.name());

        } else {
            throw new InvalidEventException("还款订单状态与事件类型不匹配，状态：" + payOrder.getStatus() + "，事件：" + event);
        }

        return payOrder;
    }
}
