package com.mo9.raptor.engine.state.pay;

import com.mo9.libracredit.engine.action.IActionExecutor;
import com.mo9.libracredit.engine.action.impl.DeductExecuteAction;
import com.mo9.libracredit.engine.entity.PayOrderEntity;
import com.mo9.libracredit.engine.enums.StatusEnum;
import com.mo9.libracredit.engine.event.IEvent;
import com.mo9.libracredit.engine.event.order.pay.DeductLaunchEvent;
import com.mo9.libracredit.engine.exception.InvalidEventException;
import com.mo9.libracredit.engine.launcher.IEventLauncher;
import com.mo9.libracredit.engine.service.IPayOrderService;
import com.mo9.libracredit.engine.state.IStateHandler;
import com.mo9.libracredit.engine.state.StateHandler;
import com.mo9.libracredit.service.CashAccountService;
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

    @Autowired
    private CashAccountService cashAccountService;

    @Autowired
    private IEventLauncher payOrderEventLauncher;

    @Override
    public PayOrderEntity handle(PayOrderEntity payOrder, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        if (event instanceof DeductLaunchEvent) {
            payOrder.setStatus(StatusEnum.DEDUCTING.name());
            /** TODO: 执行扣款 */
            actionExecutor.append(new DeductExecuteAction(payOrder.getOrderId(), payOrderService, cashAccountService, payOrderEventLauncher));

        } else {
            throw new InvalidEventException("还款订单状态与事件类型不匹配，状态：" + payOrder.getStatus() + "，事件：" + event);
        }

        return payOrder;
    }
}
