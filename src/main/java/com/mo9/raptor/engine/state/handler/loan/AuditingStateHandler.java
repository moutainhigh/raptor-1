package com.mo9.raptor.engine.state.handler.loan;

import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.state.action.impl.loan.LoanLaunchAction;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.LendModeEnum;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;
import com.mo9.raptor.engine.state.event.impl.CancelEvent;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component("loanAuditingState")
@StateHandler(name = StatusEnum.AUDITING)
public class AuditingStateHandler implements IStateHandler<LoanOrderEntity> {

    @Autowired
    private IEventLauncher loanOrderEventLauncher;

    @Override
    public LoanOrderEntity handle(LoanOrderEntity loanOrder, IEvent event, IActionExecutor actionExecutor) throws Exception {

        if (event instanceof CancelEvent) {
            loanOrder.setStatus(StatusEnum.CANCELLED.name());
        } else if (event instanceof AuditResponseEvent) {
            AuditResponseEvent auditResponseEvent = (AuditResponseEvent) event;
            if (auditResponseEvent.isPass()) {
                //审核成功
                loanOrder.setStatus(StatusEnum.PASSED.name());
                loanOrder.setAuditSignature("--");
                loanOrder.setLentTime(auditResponseEvent.getEventTime());

                if (LendModeEnum.AUTO.name().equals(loanOrder.getLendMode())) {
                    /** 自动放款模式下，则附加发起放款 */
                    actionExecutor.append(new LoanLaunchAction(loanOrder.getOrderId(), loanOrderEventLauncher));
                }
            } else {
                loanOrder.setStatus(StatusEnum.REJECTED.name());
            }
            loanOrder.setDescription(loanOrder.getDescription() + " " + event.getEventTime() + ":" + auditResponseEvent.getExplanation());

        } else {
            throw new InvalidEventException("贷款订单状态与事件类型不匹配，状态：" + loanOrder.getStatus() + "，事件：" + event);
        }

        return loanOrder;
    }
}
