package com.mo9.raptor.engine.state.action.impl.loan;

import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.engine.state.event.impl.lend.LendLaunchEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by gqwu on 2018/4/4.
 * 贷款行为-创建放款订单，并发起放款
 */
public class LoanExecuteAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(LoanExecuteAction.class);

    String orderId;

    /** 放款订单状态机事件触发器 */
    private IEventLauncher lendEventLauncher;

    public LoanExecuteAction(IEventLauncher lendEventLauncher) {
        this.lendEventLauncher = lendEventLauncher;
    }

    @Override
    public void run() {

        /** TODO：创建放款订单 */
        orderId = "---";
        try {
            LendLaunchEvent event = new LendLaunchEvent(orderId);
            lendEventLauncher.launch(event);
        } catch (Exception e) {
            logger.error("下单后, orderId: {}放款时发生错误:", orderId, e);
        }
    }

    @Override
    public String getActionType() {
        return this.getClass().getName();
    }

    @Override
    public String getOrderId() {
        return orderId;
    }
}
