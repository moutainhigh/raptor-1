package com.mo9.raptor.engine.state.action.impl.loan;

import com.alibaba.druid.support.spring.stat.annotation.Stat;
import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.engine.state.event.impl.loan.LoanLaunchEvent;
import com.mo9.raptor.engine.state.event.impl.loan.LoanResponseEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by gqwu on 2018/4/4.
 * 放款结果Action
 */
public class LoanResponseAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(LoanResponseAction.class);

    private String loanOrderId;

    private IEventLauncher loanEventLauncher;

    private ILendOrderService lendOrderService;

    public LoanResponseAction(String loanOrderId, ILendOrderService lendOrderService, IEventLauncher loanEventLauncher) {
        this.loanOrderId = loanOrderId;
        this.loanEventLauncher = loanEventLauncher;
        this.lendOrderService = lendOrderService;
    }

    @Override
    public void run(){
        LendOrderEntity lendOrderEntity = lendOrderService.getByOrderId(loanOrderId);
        String status = lendOrderEntity.getStatus();
        LoanResponseEvent loanResponse;
        if (StatusEnum.SUCCESS.name().equals(status)) {
            loanResponse = new LoanResponseEvent(
                    lendOrderEntity.getOrderId(),
                    lendOrderEntity.getChannelLendNumber(),
                    true,
                    lendOrderEntity.getChanelResponseTime(),
                    "放款成功",
                    "放款成功");
        } else {
            loanResponse = new LoanResponseEvent(
                    lendOrderEntity.getOrderId(),
                    lendOrderEntity.getChannelLendNumber(),
                    false,
                    -1L,
                    "放款失败",
                    "放款失败");
        }
        try {
            loanEventLauncher.launch(loanResponse);
        } catch (Exception e) {
            logger.error("放款通知借款订单事件异常，事件：[{}]", loanResponse, e);
        }
    }

    @Override
    public String getActionType() {
        return this.getClass().getName();
    }

    @Override
    public String getOrderId() {
        return loanOrderId;
    }
}
