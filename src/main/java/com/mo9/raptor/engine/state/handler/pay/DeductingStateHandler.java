package com.mo9.raptor.engine.state.handler.pay;

import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.state.action.impl.pay.EntryExecuteAction;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.event.impl.pay.DeductResponseEvent;
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
@StateHandler(name = StatusEnum.DEDUCTING)
public class DeductingStateHandler implements IStateHandler<PayOrderEntity> {

    private static final Logger logger = LoggerFactory.getLogger(DeductingStateHandler.class);

    @Autowired
    private IEventLauncher loanOrderEventLauncher;

    @Autowired
    private IPayOrderService payOrderService;

    @Override
    public PayOrderEntity handle(PayOrderEntity payOrder, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        if (event instanceof DeductResponseEvent) {
            DeductResponseEvent deductResponseEvent = (DeductResponseEvent) event;
            if (deductResponseEvent.isSucceeded()) {
                payOrder.setStatus(StatusEnum.ENTRY_DOING.name());
                payOrder.setPayNumber(deductResponseEvent.getActualDeducted());
                payOrder.setPayTime(deductResponseEvent.getEventTime());
                /** 此处相当于在扣款成功后，自动发起入账 */
                actionExecutor.append(new EntryExecuteAction(payOrder.getOrderId(), payOrderService, loanOrderEventLauncher));
            } else {
                payOrder.setStatus(StatusEnum.DEDUCT_FAILED.name());
            }
            payOrder.setDescription(payOrder.getDescription() + " " + event.getEventTime() + ":" + deductResponseEvent.getExplanation());
        } else {
            throw new InvalidEventException("还款订单状态与事件类型不匹配，状态：" + payOrder.getStatus() + "，事件：" + event);
        }
        return payOrder;
    }
}
