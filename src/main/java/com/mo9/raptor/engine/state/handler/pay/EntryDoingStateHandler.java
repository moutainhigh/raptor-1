package com.mo9.raptor.engine.state.handler.pay;

import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.state.action.impl.pay.RepaySuccessNoticeAction;
import com.mo9.raptor.engine.state.action.impl.pay.WhiteUserAction;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.pay.EntryResponseEvent;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import com.mo9.raptor.service.PayOrderLogService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.push.PushUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component
@StateHandler(name = StatusEnum.ENTRY_DOING)
public class EntryDoingStateHandler implements IStateHandler<PayOrderEntity> {

    @Autowired
    private PushUtils pushUtils;

    @Autowired
    private ILoanOrderService loanOrderService;

    @Autowired
    private IPayOrderService payOrderService;

    @Autowired
    private PayOrderLogService payOrderLogService;

    @Autowired
    private UserService userService;

    @Autowired
    private HttpClientApi httpClientApi;

    @Value("${raptor.white.user.base.url}")
    private String whiteBaseUrl;

    @Override
    public PayOrderEntity handle(PayOrderEntity payOrder, IEvent event, IActionExecutor actionExecutor) throws InvalidEventException {

        if (event instanceof EntryResponseEvent) {
            EntryResponseEvent entryResponseEvent = (EntryResponseEvent) event;
            payOrder.setEntryNumber(payOrder.getEntryNumber().add(entryResponseEvent.getActualEntry()));
            if (payOrder.getEntryNumber().compareTo(payOrder.getPayNumber()) == 0) {
                payOrder.setStatus(StatusEnum.ENTRY_DONE.name());
                // 发送还款成功消息
                actionExecutor.append(new RepaySuccessNoticeAction(payOrder, loanOrderService, pushUtils));
                actionExecutor.append(new WhiteUserAction(loanOrderService, payOrderService, payOrderLogService, userService, httpClientApi, payOrder.getOwnerId(), whiteBaseUrl));
            } else {
                payOrder.setStatus(StatusEnum.ENTRY_FAILED.name());
            }
            payOrder.setEntryOverTime(entryResponseEvent.getEventTime());
            payOrder.setDescription(payOrder.getDescription() + " " + event.getEventTime() + ":" + StatusEnum.valueOf(payOrder.getStatus()).getExplanation() + ";");
        }  else {
            throw new InvalidEventException(
                    "还款订单状态与事件类型不匹配，状态：" + payOrder.getStatus() +
                            "，事件：" + event);
        }
        return payOrder;
    }
}
