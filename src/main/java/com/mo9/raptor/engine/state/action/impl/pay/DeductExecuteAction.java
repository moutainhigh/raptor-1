package com.mo9.raptor.engine.state.action.impl.pay;

import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.service.IPayOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 调用渠道扣款接口，执行渠道扣款
 */
public class DeductExecuteAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(DeductExecuteAction.class);

    private String payOrderId;

    private IPayOrderService payOrderService;

    public DeductExecuteAction(String payOrderId, IPayOrderService payOrderService) {
        this.payOrderId = payOrderId;
        this.payOrderService = payOrderService;
    }

    @Override
    public void run() {

        PayOrderEntity payOrder = payOrderService.getByOrderId(payOrderId);
        String userCode = payOrder.getOwnerId();

        /** TODO:调用渠道扣款接口 */

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
