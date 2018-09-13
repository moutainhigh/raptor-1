package com.mo9.raptor.engine.state.pay;

import com.mo9.raptor.engine.action.IActionExecutor;
import com.mo9.raptor.engine.action.impl.DeductExecuteAction;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.event.IEvent;
import com.mo9.raptor.engine.event.impl.order.CancelEvent;
import com.mo9.raptor.engine.event.impl.order.pay.DeductLaunchEvent;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.launcher.IEventLauncher;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.state.IStateHandler;
import com.mo9.raptor.engine.state.StateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component
@StateHandler(name = StatusEnum.PASSED)
public class PassedStateHandler implements IStateHandler<PayOrderEntity> {

    @Autowired
    private IPayOrderService payOrderService;

    @Autowired
    private CashAccountService cashAccountService;

    @Autowired
    private IEventLauncher payOrderEventLauncher;

    @Override
    public PayOrderEntity handle(PayOrderEntity payOrder, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        if (event instanceof CancelEvent) {
            payOrder.setStatus(StatusEnum.CANCELLED.name());

        } else if (event instanceof DeductLaunchEvent) {
            payOrder.setStatus(StatusEnum.DEDUCTING.name());
            actionExecutor.append(new DeductExecuteAction(payOrder.getOrderId(), payOrderService, payOrderEventLauncher));

        } else {
            throw new InvalidEventException("还款订单状态与事件类型不匹配，状态：" + payOrder.getStatus() + "，事件：" + event);
        }

        return payOrder;
    }
}
