package com.mo9.raptor.engine.state.handler.loan;

import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.service.BillService;
import com.mo9.raptor.engine.service.IPayOrderDetailService;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.state.action.impl.loan.EntryResponseAction;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.event.impl.loan.LoanEntryEvent;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.exception.LoanEntryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component
@StateHandler(name = StatusEnum.LENT)
public class LentStateHandler implements IStateHandler<LoanOrderEntity> {

    @Autowired
    private BillService billService;

    @Autowired
    private IEventLauncher payEventLauncher;

    @Autowired
    private IEventLauncher couponEventLauncher;

    @Autowired
    private IPayOrderService payOrderService;

    @Autowired
    private IPayOrderDetailService payOrderDetailService;

    @Override
    public LoanOrderEntity handle (LoanOrderEntity loanOrder, IEvent event, IActionExecutor actionExecutor)
            throws InvalidEventException, LoanEntryException {

        if (event instanceof LoanEntryEvent) {
            LoanEntryEvent loanEntryEvent = (LoanEntryEvent) event;
            Item entryItem = loanEntryEvent.getEntryItem();
            String payType = loanEntryEvent.getPayType();
            String payOrderId = loanEntryEvent.getPayOrderId();
            PayOrderEntity payOrderEntity = payOrderService.getByOrderId(payOrderId);
            Item realItem = billService.realItem(loanOrder, PayTypeEnum.valueOf(payType), payOrderEntity.getPostponeDays());
            loanOrder = billService.itemEntry(loanOrder, PayTypeEnum.valueOf(payType), payOrderEntity.getPostponeDays(), realItem, entryItem);

            // 还款合法, 则向还款订单发送入账反馈
            actionExecutor.append(new EntryResponseAction(
                    loanEntryEvent.getPayOrderId(), loanOrder.getOrderId(), realItem, entryItem,
                    loanOrder.getOwnerId(), payOrderEntity.getPayCurrency(), payEventLauncher, payOrderDetailService, couponEventLauncher));
        } else {
            throw new InvalidEventException("贷款订单状态与事件类型不匹配，状态：" + loanOrder.getStatus() + "，事件：" + event);
        }
        return loanOrder;
    }
}
