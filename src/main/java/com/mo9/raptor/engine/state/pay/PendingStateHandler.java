package com.mo9.raptor.engine.state.pay;

import com.mo9.raptor.engine.action.IActionExecutor;
import com.mo9.raptor.engine.action.impl.PayAuditAction;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.event.IEvent;
import com.mo9.raptor.engine.event.impl.order.AuditLaunchEvent;
import com.mo9.raptor.engine.event.impl.order.CancelEvent;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.launcher.IEventLauncher;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.state.IStateHandler;
import com.mo9.raptor.engine.state.StateHandler;
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

    @Autowired
    private IPayOrderService payOrderService;

    @Override
    public PayOrderEntity handle(PayOrderEntity payOrder, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        if (event instanceof CancelEvent) {
            payOrder.setStatus(StatusEnum.CANCELLED.name());

        } else if (event instanceof AuditLaunchEvent) {

            AuditLaunchEvent auditLaunchEvent = (AuditLaunchEvent) event;

            if (!auditLaunchEvent.getUserCode().equals(payOrder.getOwnerId())) {

                logger.error("用户不是该还款订单的拥有者，事件所属用户：[{}]，订单：[{}]，订单所属用户：[{}]",
                        auditLaunchEvent.getUserCode(), payOrder.getOrderId(), payOrder.getOwnerId());

                throw new InvalidEventException("用户不是该还款订单的拥有者，事件所属用户：" + auditLaunchEvent.getUserCode()
                        + "，订单：" + payOrder.getOrderId() + "，订单所属用户：" + payOrder.getOwnerId());
            }

            payOrder.setStatus(StatusEnum.AUDITING.name());
            /** 还款订单审核 */
            actionExecutor.append(new PayAuditAction(payOrder, payOrderEventLauncher));
        } else {
            throw new InvalidEventException("还款订单状态与事件类型不匹配，状态：" + payOrder.getStatus() + "，事件：" + event);
        }

        return payOrder;
    }
}
