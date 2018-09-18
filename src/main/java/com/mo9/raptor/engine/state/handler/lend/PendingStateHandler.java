package com.mo9.raptor.engine.state.handler.lend;

import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.state.action.impl.lend.LendExecuteAction;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.lend.LendLaunchEvent;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.utils.GatewayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component("pendingState")
@StateHandler(name = StatusEnum.PENDING)
public class PendingStateHandler implements IStateHandler<LendOrderEntity> {

    @Autowired
    private ILendOrderService lendOrderService;

    @Autowired
    private GatewayUtils gatewayUtils;

    @Autowired
    private IEventLauncher lendEventLauncher;

    @Override
    public LendOrderEntity handle(LendOrderEntity lendOrder, IEvent event, IActionExecutor actionExecutor) throws Exception {

        if (event instanceof LendLaunchEvent) {
            lendOrder.setStatus(StatusEnum.LENDING.name());
            actionExecutor.append(new LendExecuteAction(lendOrder.getApplyUniqueCode(), lendOrderService, gatewayUtils, lendEventLauncher));
        }  else {
            throw new InvalidEventException("放款订单状态与事件类型不匹配，状态：" + lendOrder.getStatus() + "，事件：" + event);
        }

        return lendOrder;
    }
}
