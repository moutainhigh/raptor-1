package com.mo9.raptor.engine.state.handler.lend;

import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.lend.LendResponseEvent;
import com.mo9.raptor.engine.state.event.impl.loan.LoanResponseEvent;
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

    @Override
    public LendOrderEntity handle(LendOrderEntity lendOrder, IEvent event, IActionExecutor actionExecutor) throws Exception {

        if (event instanceof LendResponseEvent) {
            LendResponseEvent lendResponse = (LendResponseEvent) event;
            if (lendResponse.isSucceeded()) {
                lendOrder.setStatus(StatusEnum.SUCCESS.name());
                lendOrder.setChanelResponseTime(lendResponse.getSuccessTime());
                lendOrder.setChannelLendNumber(lendResponse.getActualLent());
                lendOrder.setChannelOrderId(lendResponse.getChannelOrderId());
                lendOrder.setChannelResponse(lendResponse.getChannelResponse());

                LoanResponseEvent loanResponse = new LoanResponseEvent(
                        lendOrder.getApplyUniqueCode(),
                        lendResponse.getActualLent(),
                        lendResponse.isSucceeded(),
                        lendResponse.getSuccessTime(),
                        lendResponse.getExplanation(),
                        lendResponse.getLendSignature());
                loanEventLauncher.launch(loanResponse);
            } else {
                lendOrder.setStatus(StatusEnum.FAILED.name());

            }
        }  else {
            throw new InvalidEventException("放款订单状态与事件类型不匹配，状态：" + lendOrder.getStatus() + "，事件：" + event);
        }

        return lendOrder;
    }
}
