package com.mo9.raptor.engine.state.action.impl.pay;

import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.service.BillService;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.engine.state.event.impl.loan.LoanEntryEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.exception.LoanEntryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntryExecuteAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(EntryExecuteAction.class);

    private String payOrderId;

    private IEventLauncher loanEventLauncher;

    private IPayOrderService payOrderService;

    private ILoanOrderService loanOrderService;

    private BillService billService;

    public EntryExecuteAction(String payOrderId, IPayOrderService payOrderService, ILoanOrderService loanOrderService, IEventLauncher loanEventLauncher, BillService billService) {
        this.payOrderId = payOrderId;
        this.payOrderService = payOrderService;
        this.loanOrderService = loanOrderService;
        this.loanEventLauncher = loanEventLauncher;
        this.billService = billService;
    }

    @Override
    public void run() {

        PayOrderEntity payOrderEntity = payOrderService.getByOrderId(payOrderId);
        String loanOrderId = payOrderEntity.getLoanOrderId();
        LoanOrderEntity loanOrderEntity = loanOrderService.getByOrderId(loanOrderId);
        String payType = payOrderEntity.getType();
        Item entryItem = null;
        LoanEntryEvent event = null;
        try {
            entryItem = billService.entryItem(PayTypeEnum.valueOf(payType), payOrderEntity, loanOrderEntity);
            event = new LoanEntryEvent(loanOrderId, payOrderId, payType, entryItem);
            this.loanEventLauncher.launch(event);
        } catch (Exception e) {
            logger.error("用户入账事件处理异常，事件：[{}]", event, e);
        }

    }

    @Override
    public String getActionType() {
        return this.getClass().getName();
    }

    @Override
    public String getOrderId() {
        //TODO:批次号
        return this.payOrderId;
    }
}
