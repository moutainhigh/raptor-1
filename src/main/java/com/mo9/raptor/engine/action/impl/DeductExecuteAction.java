package com.mo9.raptor.engine.action.impl;

import com.mo9.raptor.engine.action.IAction;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.event.IEvent;
import com.mo9.raptor.engine.event.impl.order.pay.DeductResponseEvent;
import com.mo9.raptor.engine.launcher.IEventLauncher;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.enums.ResCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeductExecuteAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(DeductExecuteAction.class);

    private String payOrderId;

    private IPayOrderService payOrderService;

    private IEventLauncher payOrderEventLauncher;

    public DeductExecuteAction(String payOrderId, IPayOrderService payOrderService, IEventLauncher payOrderEventLauncher) {
        this.payOrderId = payOrderId;
        this.payOrderService = payOrderService;
        this.payOrderEventLauncher = payOrderEventLauncher;
    }

    @Override
    public void run() {

        PayOrderEntity payOrder = payOrderService.getByOrderId(payOrderId);
        String userCode = payOrder.getOwnerId();

        // 判断扣款是否成功
        IEvent event;
        if (ResCodeEnum.SUCCESS.equals(resCodeEnum)) {
            event = new DeductResponseEvent(payOrderId, payOrder.getApplyNumber(), true, "扣款成功");
        } else {
            event = new DeductResponseEvent(payOrderId, payOrder.getApplyNumber(), false, "扣款失败");
        }
        try {
            payOrderEventLauncher.launch(event);
        } catch (Exception e) {
            logger.error("用户[{}]还款[{}], 入账异常", userCode, payOrderId, e);
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
