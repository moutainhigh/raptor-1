package com.mo9.raptor.engine.state.handler.pay;

import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.state.action.impl.pay.DeductExecuteAction;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;
import com.mo9.raptor.engine.state.event.impl.CancelEvent;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component
@StateHandler(name = StatusEnum.AUDITING)
class AuditingStateHandler implements IStateHandler<PayOrderEntity> {

    @Autowired
    private IPayOrderService payOrderService;

    @Override
    public PayOrderEntity handle(PayOrderEntity payOrder, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        if (event instanceof CancelEvent) {
            payOrder.setStatus(StatusEnum.CANCELLED.name());

        } else if (event instanceof AuditResponseEvent) {
            AuditResponseEvent auditResponseEvent = (AuditResponseEvent) event;
            if (auditResponseEvent.isPass()) {

                payOrder.setStatus(StatusEnum.DEDUCTING.name());

                /** 此处，相当于在审核通过后，自动触发扣款 */
                actionExecutor.append(new DeductExecuteAction(payOrder.getOrderId(), payOrderService));
            } else {
                payOrder.setStatus(StatusEnum.REJECTED.name());
            }
            payOrder.setDescription(payOrder.getDescription() + " " + event.getEventTime() + ":" + auditResponseEvent.getExplanation());

        } else {
            throw new InvalidEventException("还款订单状态与事件类型不匹配，状态：" + payOrder.getStatus() + "，事件：" + event);
        }

        return payOrder;
    }
}
