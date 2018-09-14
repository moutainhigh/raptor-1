package com.mo9.raptor.engine.state.action.impl.lend;

import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.utils.GatewayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by gqwu on 2018/4/4.
 * 调用渠道执行放款
 */
public class LendExecuteAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(LendExecuteAction.class);

    private String orderId;

    private ILendOrderService lendOrderService;

    private GatewayUtils gatewayUtils;

    public LendExecuteAction(String orderId, ILendOrderService lendOrderService, GatewayUtils gatewayUtils) {
        this.orderId = orderId;
        this.lendOrderService = lendOrderService;
        this.gatewayUtils = gatewayUtils;
    }

    @Override
    public void run() {
        try {
            LendOrderEntity lendOrder = lendOrderService.getByOrderId(orderId);
            // TODO: 补充参数
            gatewayUtils.loan();
        } catch (Exception e) {
            logger.error("下单后, orderId: {}放款时发生错误:", this.orderId, e);
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
