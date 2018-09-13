package com.mo9.raptor.engine.state.pay;

import com.mo9.libracredit.engine.action.IActionExecutor;
import com.mo9.libracredit.engine.action.impl.EntryExecuteAction;
import com.mo9.libracredit.engine.entity.PayOrderEntity;
import com.mo9.libracredit.engine.enums.PayTypeEnum;
import com.mo9.libracredit.engine.enums.StatusEnum;
import com.mo9.libracredit.engine.event.IEvent;
import com.mo9.libracredit.engine.event.order.pay.EntryLaunchEvent;
import com.mo9.libracredit.engine.exception.InvalidEventException;
import com.mo9.libracredit.engine.launcher.IEventLauncher;
import com.mo9.libracredit.engine.state.IStateHandler;
import com.mo9.libracredit.engine.state.StateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component
@StateHandler(name = StatusEnum.DEDUCTED)
public class DeductedStateHandler implements IStateHandler<PayOrderEntity> {

    @Autowired
    private IEventLauncher entryEventLauncher;

    @Override
    public PayOrderEntity handle(PayOrderEntity payOrder, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        if (event instanceof EntryLaunchEvent) {
            payOrder.setStatus(StatusEnum.ENTRY_DOING.name());
            PayTypeEnum payType = PayTypeEnum.valueOf(payOrder.getType());
            actionExecutor.append(new EntryExecuteAction(payOrder.getOwnerId(), payOrder.getBatchId(), payType, entryEventLauncher));

        } else {
            throw new InvalidEventException("还款订单状态与事件类型不匹配，状态：" + payOrder.getStatus() + "，事件：" + event);
        }

        return payOrder;
    }
}
