package com.mo9.raptor.engine.state.handler.pay;

import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.pay.EntryResponseEvent;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component
@StateHandler(name = StatusEnum.ENTRY_DOING)
public class EntryDoingStateHandler implements IStateHandler<PayOrderEntity> {

    @Override
    public PayOrderEntity handle(PayOrderEntity payOrder, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        if (event instanceof EntryResponseEvent) {
            EntryResponseEvent entryResponseEvent = (EntryResponseEvent) event;
            payOrder.setEntryNumber(payOrder.getEntryNumber().add(entryResponseEvent.getActualEntry()));
            if (payOrder.getEntryNumber().compareTo(payOrder.getPayNumber()) == 0) {
                payOrder.setStatus(StatusEnum.ENTRY_DONE.name());
            } else {
                payOrder.setStatus(StatusEnum.ENTRY_FAILED.name());
            }
            payOrder.setEntryOverTime(entryResponseEvent.getEventTime());
            payOrder.setDescription(payOrder.getDescription() + " " + event.getEventTime() + ":" + StatusEnum.valueOf(payOrder.getStatus()).getExplanation());
        }  else {
            throw new InvalidEventException(
                    "还款订单状态与事件类型不匹配，状态：" + payOrder.getStatus() +
                            "，事件：" + event);
        }
        return payOrder;
    }
}
