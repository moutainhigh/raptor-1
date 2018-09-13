package com.mo9.raptor.engine.state.pay;

import com.mo9.libracredit.engine.action.IActionExecutor;
import com.mo9.libracredit.engine.action.impl.EntryLaunchAction;
import com.mo9.libracredit.engine.entity.PayOrderEntity;
import com.mo9.libracredit.engine.enums.StatusEnum;
import com.mo9.libracredit.engine.event.IEvent;
import com.mo9.libracredit.engine.event.order.pay.DeductResponseEvent;
import com.mo9.libracredit.engine.exception.InvalidEventException;
import com.mo9.libracredit.engine.launcher.IEventLauncher;
import com.mo9.libracredit.engine.service.IPayOrderService;
import com.mo9.libracredit.engine.state.IStateHandler;
import com.mo9.libracredit.engine.state.StateHandler;
import com.mo9.libracredit.engine.state.loan.LendingStateHandler;
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

    private static final Logger logger = LoggerFactory.getLogger(LendingStateHandler.class);

    @Autowired
    private IEventLauncher payOrderEventLauncher;

    @Autowired
    private IPayOrderService payOrderService;

    @Override
    public PayOrderEntity handle(PayOrderEntity payOrder, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        if (event instanceof DeductResponseEvent) {
            DeductResponseEvent deductResponseEvent = (DeductResponseEvent) event;
            if (deductResponseEvent.isSucceeded()) {
                payOrder.setStatus(StatusEnum.DEDUCTED.name());
                payOrder.setPayNumber(deductResponseEvent.getActualDeducted());
                payOrder.setPayTime(deductResponseEvent.getEventTime());
                /** 此处相当于在扣款成功后，自动发起入账 */
                actionExecutor.append(new EntryLaunchAction(payOrder.getOrderId(), payOrderEventLauncher, payOrderService));

            } else {
                payOrder.setStatus(StatusEnum.DEDUCT_FAILED.name());
            }

            payOrder.setDescription(payOrder.getDescription() + " " + event.getEventTime() + ":" + deductResponseEvent.getExplanation());
        } else {
            throw new InvalidEventException(
                    "还款订单状态与事件类型不匹配，状态：" + payOrder.getStatus() +
                            "，事件：" + event);
        }

        return payOrder;
    }
}
