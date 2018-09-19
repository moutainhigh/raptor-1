package com.mo9.raptor.engine.state.handler.lend;

import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.state.action.impl.loan.LoanResponseAction;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.lend.LendResponseEvent;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component("lendingState")
@StateHandler(name = StatusEnum.LENDING)
public class LendingStateHandler implements IStateHandler<LendOrderEntity> {

    @Autowired
    private IEventLauncher loanEventLauncher;

    @Autowired
    private ILendOrderService lendOrderService;

    @Override
    public LendOrderEntity handle(LendOrderEntity lendOrder, IEvent event, IActionExecutor actionExecutor) throws Exception {

        if (event instanceof LendResponseEvent) {
            LendResponseEvent lendResponse = (LendResponseEvent) event;
            if (lendResponse.isSucceeded()) {
                // 放款成功
                lendOrder.setStatus(StatusEnum.SUCCESS.name());
                lendOrder.setChanelResponseTime(lendResponse.getSuccessTime());
                lendOrder.setChannelLendNumber(lendResponse.getActualLent());
                lendOrder.setDealCode(lendResponse.getChannelOrderId());
                lendOrder.setChannelResponse(lendResponse.getChannelResponse());
                lendOrder.setChannel(lendResponse.getChannel());
            } else {
                // 放款失败
                lendOrder.setStatus(StatusEnum.FAILED.name());
                lendOrder.setChanelResponseTime(lendResponse.getSuccessTime());
                lendOrder.setChannelLendNumber(lendResponse.getActualLent());
                lendOrder.setDealCode(lendResponse.getChannelOrderId());
                lendOrder.setChannelResponse(lendResponse.getChannelResponse());
                lendOrder.setChannel(lendResponse.getChannel());
                lendOrder.setFailReason(lendResponse.getFailReason());
            }
            actionExecutor.append(new LoanResponseAction(lendResponse, loanEventLauncher));
        }  else {
            throw new InvalidEventException("放款订单状态与事件类型不匹配，状态：" + lendOrder.getStatus() + "，事件：" + event);
        }
        return lendOrder;
    }
}
