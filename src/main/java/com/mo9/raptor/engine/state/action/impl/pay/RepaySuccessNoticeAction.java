package com.mo9.raptor.engine.state.action.impl.pay;

import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.utils.push.PushBean;
import com.mo9.raptor.utils.push.PushUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xzhang on 2018/10/11
 * 放款成功通知Action
 */
public class RepaySuccessNoticeAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(RepaySuccessNoticeAction.class);

    private final String orderId;

    private final String userCode;

    private PushUtils pushUtils;

    public RepaySuccessNoticeAction(String orderId, String userCode, PushUtils pushUtils) {
        this.orderId = orderId;
        this.userCode = userCode;
        this.pushUtils = pushUtils;
    }

    @Override
    public void run() {
        PushBean push = new PushBean(userCode, "还款成功", "还款成功, 有借有还再借不难~");
        // 推送
        pushUtils.push(push);
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
