package com.mo9.raptor.engine.state.loan;

import com.mo9.raptor.engine.action.IActionExecutor;
import com.mo9.raptor.engine.action.impl.LoanAuditAction;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.AuditModeEnum;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.event.IEvent;
import com.mo9.raptor.engine.event.impl.order.AuditLaunchEvent;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.launcher.IEventLauncher;
import com.mo9.raptor.engine.state.IStateHandler;
import com.mo9.raptor.engine.state.StateHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component("loanRejectedState")
@StateHandler(name = StatusEnum.REJECTED)
public class RejectedStateHandler implements IStateHandler<LoanOrderEntity> {

    @Resource(name = "loanOrderEventLauncher")
    private IEventLauncher loanOrderEventLauncher;

    @Override
    public LoanOrderEntity handle(LoanOrderEntity loanOrder, IEvent event, IActionExecutor actionExecutor) throws Exception {

        if (event instanceof AuditLaunchEvent) {
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