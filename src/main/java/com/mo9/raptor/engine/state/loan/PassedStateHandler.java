package com.mo9.raptor.engine.state.loan;

import com.mo9.raptor.engine.action.IActionExecutor;
import com.mo9.raptor.engine.action.impl.LendExecuteAction;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.LendModeEnum;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.event.IEvent;
import com.mo9.raptor.engine.event.impl.order.CancelEvent;
import com.mo9.raptor.engine.event.impl.order.loan.LendLaunchEvent;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.launcher.IEventLauncher;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.state.IStateHandler;
import com.mo9.raptor.engine.state.StateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component("loanPassedState")
@StateHandler(name = StatusEnum.PASSED)
public class PassedStateHandler implements IStateHandler<LoanOrderEntity> {

    @Autowired
    ILoanOrderService loanOrderService;

    @Autowired
    IEventLauncher loanOrderEventLauncher;

    @Override
    public LoanOrderEntity handle(LoanOrderEntity loanOrder, IEvent event, IActionExecutor actionExecutor) throws Exception {

        if (event instanceof CancelEvent) {
            loanOrder.setStatus(StatusEnum.CANCELLED.name());

        } else if (event instanceof LendLaunchEvent) {
            loanOrder.setStatus(StatusEnum.LENDING.name());
            /** 自动放款模式订单，附加执行实际放款行为（通知钱包放款）*/
            if (loanOrder.getLendMode().equals(LendModeEnum.AUTO.name())) {
                actionExecutor.append(new LendExecuteAction(loanOrder.getOrderId(), loanOrderService, loanOrderEventLauncher));
            }
        } else {
            throw new InvalidEventException("贷款订单状态与事件类型不匹配，状态：" + loanOrder.getStatus() + "，事件：" + event);
        }

        return loanOrder;
    }
}
