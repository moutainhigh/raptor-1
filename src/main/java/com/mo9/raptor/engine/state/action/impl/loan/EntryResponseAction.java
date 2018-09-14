package com.mo9.raptor.engine.state.action.impl.loan;

import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.engine.state.event.impl.pay.EntryResponseEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Created by gqwu on 2018/4/4.
 * 入账响应行为-创建还款订单入账响应事件，并发送给还款订单状态机
 */
public class EntryResponseAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(EntryResponseAction.class);

    private String payOrderId;

    private BigDecimal actualEntry;

    private IEventLauncher payEventLauncher;

    public EntryResponseAction(String payOrderId, BigDecimal actualEntry, IEventLauncher payEventLauncher) {
        this.payOrderId = payOrderId;
        this.payEventLauncher = payEventLauncher;
        this.actualEntry = actualEntry;
    }

    @Override
    public void run(){

        EntryResponseEvent event = new EntryResponseEvent(payOrderId, actualEntry);
        try {
            payEventLauncher.launch(event);
        } catch (Exception e) {
            logger.error("自动发送发起放款事件异常，事件：[{}]", event, e);
        }
    }

    @Override
    public String getActionType() {
        return this.getClass().getName();
    }

    @Override
    public String getOrderId() {
        return payOrderId;
    }
}
