package com.mo9.raptor.engine.state.handler.pay;

import com.mo9.raptor.engine.calculator.LoanCalculatorFactory;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.state.action.impl.pay.EntryExecuteAction;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.event.impl.pay.EntryLaunchEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component
@StateHandler(name = StatusEnum.ENTRY_FAILED)
public class EntryFailedStateHandler implements IStateHandler<PayOrderEntity> {

    @Autowired
    private IEventLauncher loanEventLauncher;

    @Autowired
    private IPayOrderService payOrderService;

    @Autowired
    private ILoanOrderService loanOrderService;

    @Autowired
    private LoanCalculatorFactory calculatorFactory;

    @Override
    public PayOrderEntity handle(PayOrderEntity payOrder, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        if (event instanceof EntryLaunchEvent) {
            payOrder.setStatus(StatusEnum.ENTRY_DOING.name());
            actionExecutor.append(new EntryExecuteAction(payOrder.getOrderId(), payOrderService, loanOrderService, loanEventLauncher, calculatorFactory));
        } else {
            throw new InvalidEventException("还款订单状态与事件类型不匹配，状态：" + payOrder.getStatus() + "，事件：" + event);
        }

        return payOrder;
    }
}
