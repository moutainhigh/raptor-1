package com.mo9.raptor.engine.state.pay;

import com.mo9.libracredit.engine.action.IActionExecutor;
import com.mo9.libracredit.engine.entity.PayOrderEntity;
import com.mo9.libracredit.engine.enums.StatusEnum;
import com.mo9.libracredit.engine.event.IEvent;
import com.mo9.libracredit.engine.event.order.pay.EntryResponseEvent;
import com.mo9.libracredit.engine.exception.InvalidEventException;
import com.mo9.libracredit.engine.state.IStateHandler;
import com.mo9.libracredit.engine.state.StateHandler;
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
            if (payOrder.getEntryNumber().compareTo(payOrder.getAnchorNumber()) == 0) {
                payOrder.setStatus(StatusEnum.ENTRY_DONE.name());
            } else {
                payOrder.setStatus(StatusEnum.ENTRY_FAILED.name());
            }
            payOrder.setEntryOverTime(entryResponseEvent.getEventTime());
        }  else {
            throw new InvalidEventException(
                    "还款订单状态与事件类型不匹配，状态：" + payOrder.getStatus() +
                            "，事件：" + event);
        }

        return payOrder;
    }
}
