package com.mo9.raptor.engine.state.handler.loan;

import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.state.action.impl.loan.LoanAuditAction;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.AuditModeEnum;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.AuditLaunchEvent;
import com.mo9.raptor.engine.state.event.impl.CancelEvent;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component("loanPendingState")
@StateHandler(name = StatusEnum.PENDING)
public class PendingStateHandler implements IStateHandler<LoanOrderEntity> {

    private static final Logger logger = LoggerFactory.getLogger(PendingStateHandler.class);

    @Autowired
    private IEventLauncher loanOrderEventLauncher;

    @Override
    public LoanOrderEntity handle(LoanOrderEntity loanOrder, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        if (event instanceof CancelEvent) {
            loanOrder.setStatus(StatusEnum.CANCELLED.name());

        } else if (event instanceof AuditLaunchEvent) {

            AuditLaunchEvent auditLaunchEvent = (AuditLaunchEvent) event;

            if (!auditLaunchEvent.getUserCode().equals(loanOrder.getOwnerId())) {

                logger.error("用户不是该借贷订单的拥有者，事件所属用户：[{}]，订单：[{}]，订单所属用户：[{}]",
                        auditLaunchEvent.getUserCode(), loanOrder.getOrderId(), loanOrder.getOwnerId());

                throw new InvalidEventException("用户不是该还款订单的拥有者，事件所属用户：" + auditLaunchEvent.getUserCode()
                        + "，订单：" + loanOrder.getOrderId() + "，订单所属用户：" + loanOrder.getOwnerId());
            }

            loanOrder.setStatus(StatusEnum.AUDITING.name());
            if (loanOrder.getAuditMode().equals(AuditModeEnum.AUTO.name())) {
                /** 自动审核模式的订单，则自动审核通过（目前没有业务审核），所以，附加执行一个审核通过的行为 */
                actionExecutor.append(new LoanAuditAction(loanOrder, loanOrderEventLauncher));
            }
        } else {
            throw new InvalidEventException("贷款订单状态与事件类型不匹配，状态：" + loanOrder.getStatus() + "，事件：" + event);
        }

        return loanOrder;
    }
}
