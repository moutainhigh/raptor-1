package com.mo9.raptor.engine.state.pay;

import com.mo9.raptor.engine.action.IActionExecutor;
import com.mo9.raptor.engine.action.PayAuditAction;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.event.IEvent;
import com.mo9.raptor.engine.event.pay.DeductLaunchEvent;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.launcher.IEventLauncher;
import com.mo9.raptor.engine.state.IStateHandler;
import com.mo9.raptor.engine.state.StateHandler;
import com.mo9.raptor.service.IPayOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component
@StateHandler(name = StatusEnum.PENDING)
public class PendingStateHandler implements IStateHandler<PayOrderEntity> {

    private static final Logger logger = LoggerFactory.getLogger(PendingStateHandler.class);

    @Resource(name = "payOrderEventLauncher")
    private IEventLauncher payOrderEventLauncher;

    @Override
    public PayOrderEntity handle(PayOrderEntity payOrder, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        if (event instanceof DeductLaunchEvent) {
            payOrder.setStatus(StatusEnum.DEDUCTING.name());
            /** 还款订单审核 */
            actionExecutor.append(new PayAuditAction(payOrder, payOrderEventLauncher));
        } else {
            throw new InvalidEventException("还款订单状态与事件类型不匹配，状态：" + payOrder.getStatus() + "，事件：" + event);
        }

        return payOrder;
    }
}
