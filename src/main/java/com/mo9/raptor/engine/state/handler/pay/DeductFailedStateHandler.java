package com.mo9.raptor.engine.state.handler.pay;

import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.state.action.impl.pay.DeductExecuteAction;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.pay.DeductLaunchEvent;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component
@StateHandler(name = StatusEnum.DEDUCT_FAILED)
public class DeductFailedStateHandler implements IStateHandler<PayOrderEntity> {

    @Autowired
    private IPayOrderService payOrderService;

    @Override
    public PayOrderEntity handle(PayOrderEntity payOrder, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        if (event instanceof DeductLaunchEvent) {
            payOrder.setStatus(StatusEnum.DEDUCTING.name());
            /** TODO: 执行扣款 */
            actionExecutor.append(new DeductExecuteAction(payOrder.getOrderId(), payOrderService));

        } else {
            throw new InvalidEventException("还款订单状态与事件类型不匹配，状态：" + payOrder.getStatus() + "，事件：" + event);
        }

        return payOrder;
    }
}
