package com.mo9.raptor.engine.state.action.impl.pay;

import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.utils.push.PushBean;
import com.mo9.raptor.utils.push.PushUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xzhang on 2018/10/11
 * 放款成功通知Action
 */
public class RepaySuccessNoticeAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(RepaySuccessNoticeAction.class);

    private PayOrderEntity payOrder;

    private ILoanOrderService loanOrderService;

    private PushUtils pushUtils;

    public RepaySuccessNoticeAction(PayOrderEntity payOrder, ILoanOrderService loanOrderService, PushUtils pushUtils) {
        this.payOrder = payOrder;
        this.loanOrderService = loanOrderService;
        this.pushUtils = pushUtils;
    }

    @Override
    public void run() {
        String payOrderType = payOrder.getType();
        PushBean push = null;
        if (payOrderType.equals(PayTypeEnum.REPAY_POSTPONE.name())) {
            String loanOrderId = payOrder.getLoanOrderId();
            LoanOrderEntity loanOrderEntity = loanOrderService.getByOrderId(loanOrderId);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String format = sdf.format(new Date(loanOrderEntity.getRepaymentDate()));
            push = new PushBean(payOrder.getOwnerId(), "延期成功", "延期成功，到期还款日" + format + "。");
        } else {
            push = new PushBean(payOrder.getOwnerId(), "还款成功", "还款成功，您的欠款已还清。");
        }
        // 推送
        pushUtils.push(push);
    }

    @Override
    public String getActionType() {
        return this.getClass().getName();
    }

    @Override
    public String getOrderId() {
        return payOrder.getOrderId();
    }
}
